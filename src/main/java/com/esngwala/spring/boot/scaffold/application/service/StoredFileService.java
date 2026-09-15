package com.esngwala.spring.boot.scaffold.application.service;

import com.esngwala.spring.boot.scaffold.api.dto.storedFile.StoredCreateFileDTO;
import com.esngwala.spring.boot.scaffold.api.dto.storedFile.StoredFileReadDTO;
import com.esngwala.spring.boot.scaffold.application.service.interfaces.StoredServiceFileInterface;
import com.esngwala.spring.boot.scaffold.domain.model.StoredFile;
import com.esngwala.spring.boot.scaffold.domain.model.enums.FileStatus;
import com.esngwala.spring.boot.scaffold.domain.model.enums.FileVisibility;
import com.esngwala.spring.boot.scaffold.infrastructure.persistence.repositories.plain.StoredFileRepository;
import com.esngwala.spring.boot.scaffold.infrastructure.storage.LocalStorageProvider;
import com.esngwala.spring.boot.scaffold.shared.exception.ResourceNotFoundException;
import com.esngwala.spring.boot.scaffold.shared.mapper.StoredFileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoredFileService implements StoredServiceFileInterface {
    private final StoredFileRepository repository;
    private final StoredFileMapper mapper;
    private final LocalStorageProvider storageProvider;

    @Override
    @Transactional
    public StoredFileReadDTO uploadFile(StoredCreateFileDTO dto) {
        validateUpload(dto);
        String originalName = dto.file().getOriginalFilename();
        String key = generateStorageKey(UUID.randomUUID(), sanitizeFileName(originalName), dto.category());
        boolean bytesStored = false;
        try {
            StoredFile file = StoredFile.builder().originalFileName(originalName).storageKey(key)
                    .contentType(dto.file().getContentType()).fileSizeBytes(dto.file().getSize())
                    .visibility(dto.visibility()).fileStatus(FileStatus.PENDING)
                    .category(dto.category()).metadata(dto.metadata()).build();
            StoredFile saved = repository.saveAndFlush(file);

            storageProvider.store(dto.file().getInputStream(), key, dto.file().getSize(), dto.file().getContentType());
            bytesStored = true;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCompletion(int status) {
                    if (status != STATUS_COMMITTED) storageProvider.delete(key);
                }
            });

            saved.setFileStatus(FileStatus.ACTIVE);
            repository.flush();
            log.info("File uploaded successfully: {}", saved.getId());
            return toDto(saved);
        } catch (Exception e) {
            if (bytesStored) storageProvider.delete(key);
            log.error("Failed to upload file", e);
            throw new RuntimeException("Failed to upload file: " + originalName, e);
        }
    }

    /** Generic endpoint access is limited to active, explicitly public assets. */
    @Override @Transactional(readOnly = true)
    public Resource downloadFile(UUID fileId) {
        StoredFile file = requireActive(fileId);
        if (file.getVisibility() != FileVisibility.PUBLIC) throw new ResourceNotFoundException("Public file not found: " + fileId);
        return toResource(file);
    }

    /** Business services call this after enforcing their own domain authorization. */
    @Override @Transactional(readOnly = true)
    public Resource downloadActiveFile(UUID fileId) { return toResource(requireActive(fileId)); }

    @Override @Transactional(readOnly = true)
    public StoredFileReadDTO getActiveFile(UUID fileId) { return toDto(requireActive(fileId)); }

    @Override @Transactional(readOnly = true)
    public StoredFileReadDTO getById(UUID fileId) { return toDto(repository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File not found: " + fileId))); }

    @Override @Transactional(readOnly = true)
    public List<StoredFileReadDTO> getByIds(List<UUID> fileIds) { return repository.findAllById(fileIds).stream().map(this::toDto).toList(); }

    @Override @Transactional
    public void delete(UUID fileId) {
        StoredFile file = requireActive(fileId);
        // The calling business module must first verify that no remaining relationship needs this asset.
        file.setFileStatus(FileStatus.DELETED);
        log.info("File marked as deleted: {}", fileId);
    }

    @Override @Transactional(readOnly = true)
    public String getFileUrl(UUID fileId) {
        StoredFile file = requireActive(fileId);
        if (file.getVisibility() != FileVisibility.PUBLIC) throw new ResourceNotFoundException("Public file not found: " + fileId);
        return storageProvider.getPublicUrl(fileId.toString());
    }

    private Resource toResource(StoredFile file) {
        try {
            Path path = storageProvider.resolvePath(file.getStorageKey());
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) return resource;
        } catch (Exception e) {
            log.warn("Stored file is unavailable: {}", file.getId(), e);
        }
        throw new ResourceNotFoundException("File not found: " + file.getId());
    }

    private StoredFileReadDTO toDto(StoredFile file) {
        StoredFileReadDTO dto = mapper.toDto(file);
        String url = file.getFileStatus() == FileStatus.ACTIVE && file.getVisibility() == FileVisibility.PUBLIC
                ? storageProvider.getPublicUrl(file.getId().toString()) : null;
        return new StoredFileReadDTO(dto.id(), dto.originalFileName(), dto.contentType(), dto.fileSizeBytes(), dto.checksum(),
                dto.visibility(), dto.fileStatus(), dto.category(), dto.metadata(), url, dto.createdAt(), dto.expiresAt());
    }

    private StoredFile requireActive(UUID id) {
        StoredFile file = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("File not found: " + id));
        if (file.getFileStatus() != FileStatus.ACTIVE || (file.getExpiresAt() != null && file.getExpiresAt().isBefore(LocalDateTime.now())))
            throw new ResourceNotFoundException("File not found: " + id);
        return file;
    }

    private void validateUpload(StoredCreateFileDTO dto) {
        if (dto.file().isEmpty()) throw new IllegalArgumentException("File must not be empty");
        if (dto.file().getSize() > storageProvider.getMaxFileSizeBytes()) throw new IllegalArgumentException("File exceeds the configured maximum size");
        String name = dto.file().getOriginalFilename();
        if (name == null || name.isBlank() || name.length() > 255) throw new IllegalArgumentException("File name is required and must not exceed 255 characters");
        String type = dto.file().getContentType();
        if (type == null || type.isBlank() || type.length() > 100) throw new IllegalArgumentException("A valid content type is required");
        if (dto.category() != null && !dto.category().matches("[a-z0-9][a-z0-9-]{0,48}")) throw new IllegalArgumentException("Category must be a lowercase storage purpose identifier");
        if (dto.metadata() != null && dto.metadata().length() > 65_535) throw new IllegalArgumentException("Metadata exceeds the supported size");
    }

    private String generateStorageKey(UUID keyId, String fileName, String category) {
        int dot = fileName.lastIndexOf('.');
        return (category == null ? "" : category + "/") + keyId + (dot > 0 ? fileName.substring(dot) : "");
    }

    private String sanitizeFileName(String fileName) { return fileName.replaceAll("[^a-zA-Z0-9._-]", "_"); }
}

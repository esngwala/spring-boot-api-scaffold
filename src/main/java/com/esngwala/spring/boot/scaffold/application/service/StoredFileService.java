package com.esngwala.spring.boot.scaffold.application.service;

import com.esngwala.spring.boot.scaffold.api.dto.storedFile.StoredCreateFileDTO;
import com.esngwala.spring.boot.scaffold.api.dto.storedFile.StoredFileReadDTO;
import com.esngwala.spring.boot.scaffold.application.service.interfaces.StoredServiceFileInterface;
import com.esngwala.spring.boot.scaffold.domain.model.StoredFile;
import com.esngwala.spring.boot.scaffold.domain.model.enums.FileStatus;
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

import java.nio.file.Path;
import java.nio.file.Paths;
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
        String originalFileName = dto.file().getOriginalFilename();
        String sanitizedFileName = sanitizeFileName(originalFileName);
        
        // Generate UUID manually
        UUID fileId = UUID.randomUUID();
        String storageKey = generateStorageKey(fileId, sanitizedFileName, dto.category());

        try {
            // Store file to disk first
            storageProvider.store(
                    dto.file().getInputStream(),
                    storageKey,
                    dto.file().getSize(),
                    dto.file().getContentType()
            );

            // Create entity with manually assigned ID
            StoredFile storedFile = StoredFile.builder()
                    .originalFileName(originalFileName)
                    .storageKey(storageKey)
                    .contentType(dto.file().getContentType())
                    .fileSizeBytes(dto.file().getSize())
                    .visibility(dto.visibility())
                    .fileStatus(FileStatus.ACTIVE)
                    .category(dto.category())
                    .metadata(dto.metadata())
                    .build();

            // Manually set the ID before persisting
            storedFile.setId(fileId);
            
            // Save to database
            StoredFile saved = repository.save(storedFile);

            log.info("File uploaded successfully: {}", fileId);

            return toDtoWithUrl(saved);
        } catch (Exception e) {
            log.error("Failed to upload file", e);
            throw new RuntimeException("Failed to upload file: " + originalFileName, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadFile(UUID fileId) {
        StoredFile storedFile = repository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found: " + fileId));

        try {
            Path filePath = Paths.get(storageProvider.getBasePath()).resolve(storedFile.getStorageKey());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found or not readable: " + fileId);
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to load file: " + fileId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public StoredFileReadDTO getById(UUID fileId) {
        StoredFile storedFile = repository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found: " + fileId));

        return toDtoWithUrl(storedFile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoredFileReadDTO> getByIds(List<UUID> fileIds) {
        return repository.findAllById(fileIds)
                .stream()
                .map(this::toDtoWithUrl)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID fileId) {
        StoredFile storedFile = repository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found: " + fileId));

        storedFile.setFileStatus(FileStatus.DELETED);
        repository.save(storedFile);

        log.info("File marked as deleted: {}", fileId);
    }

    @Override
    @Transactional(readOnly = true)
    public String getFileUrl(UUID fileId) {
        StoredFile storedFile = repository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found: " + fileId));

        return storageProvider.getPublicUrl(storedFile.getStorageKey());
    }

    private StoredFileReadDTO toDtoWithUrl(StoredFile storedFile) {
        StoredFileReadDTO dto = mapper.toDto(storedFile);
        String url = storageProvider.getPublicUrl(storedFile.getStorageKey());

        return new StoredFileReadDTO(
                dto.id(),
                dto.originalFileName(),
                dto.storageKey(),
                dto.contentType(),
                dto.fileSizeBytes(),
                dto.checksum(),
                dto.visibility(),
                dto.fileStatus(),
                dto.category(),
                dto.metadata(),
                url,  // Set URL here
                dto.createdAt(),
                dto.expiresAt()
        );
    }

    private String generateStorageKey(UUID fileId, String fileName, String category) {
        String extension = "";
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            extension = fileName.substring(lastDot);
        }

        String basePath = category != null ? category + "/" : "";
        return basePath + fileId + extension;
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "unnamed";
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}

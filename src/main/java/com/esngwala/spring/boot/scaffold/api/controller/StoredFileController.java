package com.esngwala.spring.boot.scaffold.api.controller;

import com.esngwala.spring.boot.scaffold.api.dto.storedFile.StoredCreateFileDTO;
import com.esngwala.spring.boot.scaffold.api.dto.storedFile.StoredFileReadDTO;
import com.esngwala.spring.boot.scaffold.application.service.interfaces.StoredServiceFileInterface;
import com.esngwala.spring.boot.scaffold.domain.model.enums.FileVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class StoredFileController {

    private final StoredServiceFileInterface storedFileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StoredFileReadDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "visibility", defaultValue = "PRIVATE") FileVisibility visibility,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "metadata", required = false) String metadata) {

        StoredCreateFileDTO dto = new StoredCreateFileDTO(file, visibility, category, metadata);
        StoredFileReadDTO result = storedFileService.uploadFile(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID id) {
        Resource file = storedFileService.downloadFile(id);
        StoredFileReadDTO metadata = storedFileService.getById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(sanitizeDownloadName(metadata.originalFileName()), StandardCharsets.UTF_8).build().toString())
                .contentType(MediaType.parseMediaType(metadata.contentType()))
                .body(file);
    }

    @GetMapping("/{id}/info")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StoredFileReadDTO> getFileInfo(@PathVariable UUID id) {
        return ResponseEntity.ok(storedFileService.getById(id));
    }

    @GetMapping("/{id}/url")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getFileUrl(@PathVariable UUID id) {
        String url = storedFileService.getFileUrl(id);
        return ResponseEntity.ok(url);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFile(@PathVariable UUID id) {
        storedFileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private String sanitizeDownloadName(String name) {
        return name.replaceAll("[\\r\\n\\\"\\\\]", "_");
    }
}

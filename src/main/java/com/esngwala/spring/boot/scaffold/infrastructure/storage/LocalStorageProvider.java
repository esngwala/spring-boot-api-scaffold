package com.esngwala.spring.boot.scaffold.infrastructure.storage;

import com.esngwala.spring.boot.scaffold.infrastructure.config.properties.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalStorageProvider {

    private final com.esngwala.spring.boot.scaffold.infrastructure.config.properties.StorageProperties storageProperties;

    public String store(InputStream inputStream, String key, long size, String contentType) {
        Path temporaryPath = null;
        try {
            Path filePath = resolveStoragePath(key);
            Files.createDirectories(filePath.getParent());
            temporaryPath = Files.createTempFile(filePath.getParent(), ".upload-", ".tmp");
            Files.copy(inputStream, temporaryPath, StandardCopyOption.REPLACE_EXISTING);
            try {
                Files.move(temporaryPath, filePath, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporaryPath, filePath);
            }
            log.info("Stored file: {}", key);
            return key;
        } catch (IOException e) {
            if (temporaryPath != null) {
                try {
                    Files.deleteIfExists(temporaryPath);
                } catch (IOException cleanupException) {
                    e.addSuppressed(cleanupException);
                }
            }
            throw new RuntimeException("Failed to store file: " + key, e);
        }
    }

    public boolean exists(String key) {
        Path filePath = resolveStoragePath(key);
        return Files.exists(filePath);
    }

    /** Removes an object written during a failed upload. */
    public void delete(String key) {
        try {
            Files.deleteIfExists(resolveStoragePath(key));
        } catch (IOException e) {
            log.warn("Failed to remove stored file after an unsuccessful upload: {}", key, e);
        }
    }

    public String getPublicUrl(String key) {

        return storageProperties.getLocal().getPublicUrlPrefix() + "/" + key;
    }

    public String getBasePath() {

        return storageProperties.getLocal().getBasePath();
    }

    private Path resolveStoragePath(String key) {
        Path basePath = Paths.get(storageProperties.getLocal().getBasePath())
                .toAbsolutePath()
                .normalize();
        Path filePath = basePath.resolve(key).normalize();

        if (!filePath.startsWith(basePath)) {
            throw new IllegalArgumentException("Storage key must resolve within the configured storage directory");
        }
        return filePath;
    }

    public long getMaxFileSizeBytes() {
        return storageProperties.getMaxFileSizeBytes();
    }

    public Path resolvePath(String key) {
        return resolveStoragePath(key);
    }
}

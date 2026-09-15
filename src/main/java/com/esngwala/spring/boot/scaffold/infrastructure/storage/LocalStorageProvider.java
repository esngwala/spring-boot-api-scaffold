package com.esngwala.spring.boot.scaffold.infrastructure.storage;

import com.esngwala.spring.boot.scaffold.infrastructure.config.properties.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalStorageProvider {

    private final com.esngwala.spring.boot.scaffold.infrastructure.config.properties.StorageProperties storageProperties;

    public String store(InputStream inputStream, String key, long size, String contentType) {
        try {
            Path filePath = resolveStoragePath(key);
            Files.createDirectories(filePath.getParent());
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored file: {}", key);
            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + key, e);
        }
    }

    public boolean exists(String key) {
        Path filePath = resolveStoragePath(key);
        return Files.exists(filePath);
    }

    public String getPublicUrl(String key) {

        return storageProperties.getLocal().getPublicUrlPrefix() + "/" + key;
    }

    public String getBasePath() {

        return storageProperties.getLocal().getBasePath();
    }

    private Path resolveStoragePath(String key) {
        return Paths.get(storageProperties.getLocal().getBasePath()).resolve(key);
    }
}

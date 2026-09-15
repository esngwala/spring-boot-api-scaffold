package com.esngwala.spring.boot.scaffold.infrastructure.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {
    private String type = "local";
    private long maxFileSizeBytes = 10 * 1024 * 1024;

    private LocalStorageProperties local = new LocalStorageProperties();

    @Data
    public static class LocalStorageProperties {
        private String basePath = "./uploads";
        private String publicUrlPrefix = "/files";
    }
}

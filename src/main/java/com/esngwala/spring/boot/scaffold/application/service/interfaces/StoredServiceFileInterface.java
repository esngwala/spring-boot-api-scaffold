package com.esngwala.spring.boot.scaffold.application.service.interfaces;

import com.esngwala.spring.boot.scaffold.api.dto.storedFile.StoredCreateFileDTO;
import com.esngwala.spring.boot.scaffold.api.dto.storedFile.StoredFileReadDTO;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.UUID;

public interface StoredServiceFileInterface {
    StoredFileReadDTO uploadFile(StoredCreateFileDTO dto);
    Resource downloadActiveFile(UUID fileId);
    Resource downloadFile(UUID fileId);
    StoredFileReadDTO getActiveFile(UUID fileId);
    StoredFileReadDTO getById(UUID fileId);
    List<StoredFileReadDTO> getByIds(List<UUID> fileIds);
    void delete(UUID fileId);
    String getFileUrl(UUID fileId);
}

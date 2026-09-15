package com.esngwala.spring.boot.scaffold.shared.mapper;

import com.esngwala.spring.boot.scaffold.api.dto.storedFile.StoredFileReadDTO;
import com.esngwala.spring.boot.scaffold.domain.model.StoredFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StoredFileMapper {

    @Mapping(target = "url", ignore = true)
    StoredFileReadDTO toDto(StoredFile storedFile);
    List<StoredFileReadDTO> toDtoList(List<StoredFile> storedFiles);
}

package com.clara.backend_challenge.infrastructure.adapters.output.mappers;

import com.clara.backend_challenge.core.domain.Release;
import com.clara.backend_challenge.infrastructure.persistence.entity.ReleaseEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReleaseMapper {

    Release toDomain(ReleaseEntity releaseEntity);

    ReleaseEntity toEntity(Release release);
}
package com.clara.backend_challenge.infrastructure.adapters.external.mappers;

import com.clara.backend_challenge.core.domain.Release;
import com.clara.backend_challenge.infrastructure.adapters.external.dto.DiscogsReleaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiscogsReleaseMapper {

    @Mapping(target = "thumbnail", source = "thumb")
    Release toDomain(DiscogsReleaseResponse.ReleaseDto releaseDto);
}
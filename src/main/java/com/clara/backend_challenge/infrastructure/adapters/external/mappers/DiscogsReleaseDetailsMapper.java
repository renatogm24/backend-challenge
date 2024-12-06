package com.clara.backend_challenge.infrastructure.adapters.external.mappers;

import com.clara.backend_challenge.core.domain.Release;
import com.clara.backend_challenge.infrastructure.adapters.external.dto.DiscogsReleaseDetailsResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscogsReleaseDetailsMapper {

    Release toDomain(DiscogsReleaseDetailsResponse dto);
}

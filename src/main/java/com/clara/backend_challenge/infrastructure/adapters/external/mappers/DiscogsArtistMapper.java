package com.clara.backend_challenge.infrastructure.adapters.external.mappers;

import com.clara.backend_challenge.core.domain.Artist;
import com.clara.backend_challenge.infrastructure.adapters.external.dto.DiscogsArtistResponse;
import com.clara.backend_challenge.infrastructure.adapters.external.dto.DiscogsSearchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiscogsArtistMapper {

    @Mapping(source = "title", target = "name")
    @Mapping(source = "thumb", target = "primaryImage")
    Artist toDomain(DiscogsSearchResponse.DiscogsArtist discogsArtist);

    @Mapping(source = "images", target = "primaryImage", qualifiedByName = "mapFirstImage")
    @Mapping(target = "releases", ignore = true)
    Artist toDomain(DiscogsArtistResponse response);

    @Named("mapFirstImage")
    default String mapFirstImage(List<DiscogsArtistResponse.ImageDto> images) {
        if (images != null && !images.isEmpty()) {
            return images.get(0).getUri();
        }
        return null;
    }
}
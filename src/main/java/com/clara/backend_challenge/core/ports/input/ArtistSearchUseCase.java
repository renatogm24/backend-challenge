package com.clara.backend_challenge.core.ports.input;

import com.clara.backend_challenge.core.domain.Artist;

import java.util.List;

public interface ArtistSearchUseCase {
    List<Artist> searchArtists(String name);
    Artist getArtistDetails(Long artistId);
}

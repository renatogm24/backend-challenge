package com.clara.backend_challenge.core.ports.output;

import com.clara.backend_challenge.core.domain.Release;
import com.clara.backend_challenge.core.domain.Artist;

import java.util.List;

public interface DiscogsApiClient {
    List<Artist> searchArtists(String name);
    Artist getArtistDetails(Long artistId);
    List<Release> getReleasesByArtist(Long artistId, int page, int size);
    Release getReleaseDetails(Long releaseId);
    Integer getTotalItemsReleaseByArtist(Long artistId);
}

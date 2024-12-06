package com.clara.backend_challenge.infrastructure.adapters.input;

import com.clara.backend_challenge.core.domain.Artist;
import com.clara.backend_challenge.core.ports.input.ArtistSearchUseCase;
import com.clara.backend_challenge.infrastructure.adapters.input.dto.ArtistRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistSearchUseCase artistSearchUseCase;

    public ArtistController(ArtistSearchUseCase artistSearchUseCase) {
        this.artistSearchUseCase = artistSearchUseCase;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Artist>> searchArtists(@RequestParam String name) {
        List<Artist> artists = artistSearchUseCase.searchArtists(name);
        return ResponseEntity.ok(artists);
    }

    @PostMapping("/{artistId}")
    public ResponseEntity<Artist> fetchAndSaveArtistDetails(
            @PathVariable Long artistId,
            @RequestBody ArtistRequest request) {
        Artist artist = artistSearchUseCase.getArtistDetails(
                artistId,
                request.isFetchAll(),
                request.isForce(),
                request.getLimit(),
                request.getPage(),
                request.getSize()
        );
        return ResponseEntity.ok(artist);
    }
}

package com.clara.backend_challenge.infrastructure.adapters.input;

import com.clara.backend_challenge.core.domain.ArtistComparisonResult;
import com.clara.backend_challenge.core.ports.input.ArtistComparisonUseCase;
import com.clara.backend_challenge.infrastructure.adapters.input.dto.ArtistComparisonRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comparison")
public class ArtistComparisonController {

    private final ArtistComparisonUseCase artistComparisonUseCase;

    public ArtistComparisonController(ArtistComparisonUseCase artistComparisonUseCase) {
        this.artistComparisonUseCase = artistComparisonUseCase;
    }

    @PostMapping
    public ResponseEntity<ArtistComparisonResult> compareArtists(@RequestBody ArtistComparisonRequest request) {
        List<Long> artistIds = request.getArtistIds();
        ArtistComparisonResult result = artistComparisonUseCase.compareArtists(artistIds);
        return ResponseEntity.ok(result);
    }
}

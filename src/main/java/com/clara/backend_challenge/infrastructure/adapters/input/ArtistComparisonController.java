package com.clara.backend_challenge.infrastructure.adapters.input;

import com.clara.backend_challenge.core.domain.ArtistComparisonResult;
import com.clara.backend_challenge.core.ports.input.ArtistComparisonUseCase;
import com.clara.backend_challenge.infrastructure.adapters.input.dto.ArtistComparisonRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comparison")
@Tag(name = "Artist Comparison", description = "Endpoints for comparing artists based on various criteria.")
public class ArtistComparisonController {

    private final ArtistComparisonUseCase artistComparisonUseCase;

    public ArtistComparisonController(ArtistComparisonUseCase artistComparisonUseCase) {
        this.artistComparisonUseCase = artistComparisonUseCase;
    }

    @PostMapping
    @Operation(
            summary = "Compare multiple artists",
            description = "Compares artists by their releases, active years, and common genres based on their IDs."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comparison results generated successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ArtistComparisonResult.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request. At least two artist IDs are required."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    public ResponseEntity<ArtistComparisonResult> compareArtists(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request containing the list of artist IDs to compare.",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtistComparisonRequest.class)))
            @RequestBody ArtistComparisonRequest request) {
        List<Long> artistIds = request.getArtistIds();
        ArtistComparisonResult result = artistComparisonUseCase.compareArtists(artistIds);
        return ResponseEntity.ok(result);
    }
}

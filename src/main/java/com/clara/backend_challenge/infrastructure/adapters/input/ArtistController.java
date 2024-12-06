package com.clara.backend_challenge.infrastructure.adapters.input;

import com.clara.backend_challenge.core.domain.Artist;
import com.clara.backend_challenge.core.ports.input.ArtistSearchUseCase;
import com.clara.backend_challenge.infrastructure.adapters.input.dto.ArtistRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/artists")
@Tag(name = "Artist Management", description = "Endpoints for searching artists and their discography.")
public class ArtistController {

    private final ArtistSearchUseCase artistSearchUseCase;

    public ArtistController(ArtistSearchUseCase artistSearchUseCase) {
        this.artistSearchUseCase = artistSearchUseCase;
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search artists by name",
            description = "Fetches a list of artists matching the given name."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of artists retrieved successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    public ResponseEntity<List<Artist>> searchArtists(
            @Parameter(description = "The name of the artist to search for.", required = true)
            @RequestParam String name) {
        List<Artist> artists = artistSearchUseCase.searchArtists(name);
        return ResponseEntity.ok(artists);
    }

    @PostMapping("/{artistId}")
    @Operation(
            summary = "Fetch and save artist details",
            description = "Fetches details of an artist from an external Discogs API and saves them to the database."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artist details fetched and saved successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request."),
            @ApiResponse(responseCode = "404", description = "Artist not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    public ResponseEntity<Artist> fetchAndSaveArtistDetails(
            @Parameter(description = "The ID of the artist to fetch details for.", required = true)
            @PathVariable Long artistId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request payload containing options for fetching artist details.",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtistRequest.class)))
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

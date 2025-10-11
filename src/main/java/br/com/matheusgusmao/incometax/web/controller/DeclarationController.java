package br.com.matheusgusmao.incometax.web.controller;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.domain.service.DeclarationService;
import br.com.matheusgusmao.incometax.infra.persistence.entity.user.UserEntity;
import br.com.matheusgusmao.incometax.web.dto.declaration.CreateDeclarationRequest;
import br.com.matheusgusmao.incometax.web.dto.declaration.DeclarationHistoryResponse;
import br.com.matheusgusmao.incometax.web.dto.declaration.DeclarationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("declarations")
@Tag(name = "Operations involving Declaration entity.")
public class DeclarationController {

    private final DeclarationService declarationService;

    public DeclarationController(final DeclarationService declarationService) {
        this.declarationService = declarationService;
    }

    @Operation(
            summary = "Create a new tax declaration.",
            description = "Returns a created declaration."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successful operation.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateDeclarationRequest.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Already exists declaration for that year.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication fails.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @PostMapping
    public ResponseEntity<DeclarationResponse> create(@RequestBody CreateDeclarationRequest request, @AuthenticationPrincipal UserEntity authenticatedUser) {

        UUID taxpayerId = authenticatedUser.getId();

        Declaration newDeclaration = declarationService.createNewDeclaration(taxpayerId, request.year());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newDeclaration.getId()).toUri();

        return ResponseEntity.created(location).body(DeclarationResponse.from(newDeclaration));
    }
    @GetMapping("/declarations/history")
    public ResponseEntity<?> getHistory(@RequestParam UUID taxpayerId) {
        List<DeclarationHistoryResponse> history = declarationService.getDeclarationHistory(taxpayerId);
        return ResponseEntity.ok(history);
    }
    @PutMapping("/{id}/submit")
    public ResponseEntity<DeclarationResponse> submitDeclaration(@PathVariable Long id) {
        Declaration declaration = declarationService.submitDeclaration(id);
        return ResponseEntity.ok(DeclarationResponse.from(declaration));
    }
}
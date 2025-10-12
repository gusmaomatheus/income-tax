package br.com.matheusgusmao.incometax.web.controller;

import br.com.matheusgusmao.incometax.domain.model.dependent.Cpf;
import br.com.matheusgusmao.incometax.domain.model.dependent.Dependent;
import br.com.matheusgusmao.incometax.domain.service.DeclarationService;
import br.com.matheusgusmao.incometax.web.dto.declaration.DeclarationResponse;
import br.com.matheusgusmao.incometax.web.dto.dependent.CreateDependentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/declarations/{declarationId}/dependents")
@Tag(name = "Dependent Management")
public class DependentController {

    private final DeclarationService declarationService;

    public DependentController(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }

    @Operation(summary = "Add dependent to declaration")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dependent added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid CPF or duplicate dependent"),
            @ApiResponse(responseCode = "404", description = "Declaration not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PostMapping
    public ResponseEntity<DeclarationResponse> addDependent(
            @PathVariable Long declarationId,
            @Valid @RequestBody CreateDependentRequest request) {
        var cpf = new Cpf(request.cpf());
        var dependent = new Dependent(request.name(), cpf, request.birthDate());
        var response = declarationService.addDependent(declarationId, dependent);

        return ResponseEntity.status(201).body(DeclarationResponse.from(response));
    }
}
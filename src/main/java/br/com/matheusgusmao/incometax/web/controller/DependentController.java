package br.com.matheusgusmao.incometax.web.controller;

import br.com.matheusgusmao.incometax.domain.service.DependentService;
import br.com.matheusgusmao.incometax.web.dto.dependent.CreateDependentRequest;
import br.com.matheusgusmao.incometax.web.dto.dependent.DependentResponse;
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

    private final DependentService dependentService;

    public DependentController(DependentService dependentService) {
        this.dependentService = dependentService;
    }

    @Operation(summary = "Add dependent to declaration")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dependent added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid CPF or duplicate dependent"),
            @ApiResponse(responseCode = "404", description = "Declaration not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PostMapping
    public ResponseEntity<DependentResponse> addDependent(
            @PathVariable Long declarationId,
            @Valid @RequestBody CreateDependentRequest request) {
        var response = dependentService.addDependent(declarationId, request);
        return ResponseEntity.status(201).body(response);
    }
}
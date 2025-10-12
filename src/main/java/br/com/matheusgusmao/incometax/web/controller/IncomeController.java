package br.com.matheusgusmao.incometax.web.controller;

import br.com.matheusgusmao.incometax.domain.model.income.Income;
import br.com.matheusgusmao.incometax.domain.service.DeclarationService;
import br.com.matheusgusmao.incometax.infra.persistence.entity.user.UserEntity;
import br.com.matheusgusmao.incometax.web.dto.income.CreateIncomeRequest;
import br.com.matheusgusmao.incometax.web.dto.income.IncomeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/declarations/{declarationId}/incomes")
@Tag(name = "Income Management")
public class IncomeController {

    private final DeclarationService declarationService;

    public IncomeController(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }

    @Operation(summary = "Add income to declaration")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Income added successfully"),
            @ApiResponse(responseCode = "404", description = "Declaration not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PostMapping
    public ResponseEntity<IncomeResponse> addIncome(
            @PathVariable Long declarationId,
            @Valid @RequestBody CreateIncomeRequest request,
            @AuthenticationPrincipal UserEntity authenticatedUser) {
        
        var income = new Income(request.payingSource(), request.type(), request.value());
        var updatedDeclaration = declarationService.addIncome(declarationId, income);
        
        var addedIncome = updatedDeclaration.getIncomes().stream()
                .filter(i -> i.getPayingSource().equals(request.payingSource()) && 
                           i.getType().equals(request.type()) && 
                           i.getValue().equals(request.value()))
                .findFirst()
                .orElseThrow();
        
        return ResponseEntity.status(201).body(IncomeResponse.from(addedIncome));
    }
}

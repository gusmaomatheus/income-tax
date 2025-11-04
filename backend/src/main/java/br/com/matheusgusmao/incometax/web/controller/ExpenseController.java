package br.com.matheusgusmao.incometax.web.controller;

import br.com.matheusgusmao.incometax.domain.model.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.domain.service.DeclarationService;
import br.com.matheusgusmao.incometax.infra.persistence.entity.user.UserEntity;
import br.com.matheusgusmao.incometax.web.dto.expense.CreateExpenseRequest;
import br.com.matheusgusmao.incometax.web.dto.expense.ExpenseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/declarations/{declarationId}/expenses")
@Tag(name = "Deductible Expense Management")
public class ExpenseController {

    private final DeclarationService declarationService;

    public ExpenseController(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }

    @Operation(summary = "Add deductible expense to declaration")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Expense added successfully"),
            @ApiResponse(responseCode = "404", description = "Declaration not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense(
            @PathVariable Long declarationId,
            @Valid @RequestBody CreateExpenseRequest request,
            @AuthenticationPrincipal UserEntity authenticatedUser) {
        
        var expense = new DeductibleExpense(request.description(), request.type(), request.value());
        var updatedDeclaration = declarationService.addDeductibleExpense(declarationId, expense);
        
        var addedExpense = updatedDeclaration.getDeductibleExpenses().stream()
                .filter(e -> e.getDescription().equals(request.description()) && 
                           e.getType().equals(request.type()) && 
                           e.getValue().equals(request.value()))
                .findFirst()
                .orElseThrow();
        
        return ResponseEntity.status(201).body(ExpenseResponse.from(addedExpense));
    }
}

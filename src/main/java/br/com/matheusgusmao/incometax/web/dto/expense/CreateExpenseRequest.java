package br.com.matheusgusmao.incometax.web.dto.expense;

import br.com.matheusgusmao.incometax.domain.model.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.domain.model.expense.ExpenseType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateExpenseRequest(
        @NotBlank(message = "Description cannot be blank")
        String description,
        @NotNull(message = "Expense type cannot be null")
        ExpenseType type,
        @NotNull(message = "Value cannot be null")
        @DecimalMin(value = "0.01", message = "Value must be greater than zero")
        BigDecimal value
) {}

public record ExpenseResponse(
        Long id,
        String description,
        ExpenseType type,
        BigDecimal value
) {
    public static ExpenseResponse from(DeductibleExpense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getType(),
                expense.getValue()
        );
    }
}

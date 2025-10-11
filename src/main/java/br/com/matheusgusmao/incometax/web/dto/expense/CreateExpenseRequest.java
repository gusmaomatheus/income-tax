package br.com.matheusgusmao.incometax.web.dto.expense;

import br.com.matheusgusmao.incometax.domain.model.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.domain.model.expense.ExpenseType;

import java.math.BigDecimal;

public record CreateExpenseRequest(
        String description,
        ExpenseType type,
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

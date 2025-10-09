package br.com.matheusgusmao.incometax.web.dto.expense;

import br.com.matheusgusmao.incometax.domain.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.domain.expense.ExpenseType;

import java.math.BigDecimal;

public record ExpenseResponse(Long id, String description, ExpenseType type, BigDecimal value) {
    public static ExpenseResponse from(DeductibleExpense domain) {
        return new ExpenseResponse(domain.getId(), domain.getDescription(), domain.getType(), domain.getValue());
    }
}

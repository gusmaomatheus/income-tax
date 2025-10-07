package br.com.matheusgusmao.incometax.web.dto.expense;

import br.com.matheusgusmao.incometax.domain.expense.ExpenseType;

import java.math.BigDecimal;

public record AddExpenseRequest(String description, ExpenseType type, BigDecimal value) {

}

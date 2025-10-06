package br.com.matheusgusmao.incometax.web.dto.income;

import br.com.matheusgusmao.incometax.domain.model.income.IncomeType;

import java.math.BigDecimal;

public record AddIncomeRequest(String payingSource, IncomeType type, BigDecimal value) {
}

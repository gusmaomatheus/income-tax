package br.com.matheusgusmao.incometax.web.dto.income;

import br.com.matheusgusmao.incometax.domain.model.income.Income;
import br.com.matheusgusmao.incometax.domain.model.income.IncomeType;

import java.math.BigDecimal;

public record IncomeResponse(Long id, String payingSource, IncomeType type, BigDecimal value) {

    public static IncomeResponse from(Income income) {
        return new IncomeResponse(
                income.getId(),
                income.getPayingSource(),
                income.getType(),
                income.getValue()
        );
    }
}

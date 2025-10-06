package br.com.matheusgusmao.incometax.domain.model.income;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Income {
    private Long id;
    private String payingSource;
    private IncomeType type;
    private BigDecimal value;

    public Income(String payingSource, IncomeType type, BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Income value cannot be negative.");
        }
        if (payingSource == null || payingSource.isBlank()) {
            throw new IllegalArgumentException("Paying source cannot be empty.");
        }
        this.payingSource = payingSource;
        this.type = type;
        this.value = value;
    }

    public Income(Long id, String payingSource, IncomeType type, BigDecimal value) {
        this(payingSource, type, value);
        this.id = id;
    }

}

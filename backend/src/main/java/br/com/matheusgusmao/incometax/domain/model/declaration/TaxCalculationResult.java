package br.com.matheusgusmao.incometax.domain.model.declaration;

import java.math.BigDecimal;

public record TaxCalculationResult(
        BigDecimal totalIncome,
        BigDecimal totalDeductions,
        BigDecimal calculationBase,
        BigDecimal taxDue,
        BigDecimal effectiveAliquot,
        BigDecimal finalBalance
) {
    public TaxCalculationResult(BigDecimal totalIncome, BigDecimal totalDeductions, BigDecimal calculationBase, BigDecimal taxDue, BigDecimal effectiveAliquot) {
        this(totalIncome, totalDeductions, calculationBase, taxDue, effectiveAliquot, taxDue);
    }
}
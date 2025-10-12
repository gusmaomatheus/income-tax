package br.com.matheusgusmao.incometax.web.dto.income;

import br.com.matheusgusmao.incometax.domain.model.income.IncomeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateIncomeRequest(
        @NotBlank(message = "Paying source cannot be blank")
        String payingSource,
        @NotNull(message = "Income type cannot be null")
        IncomeType type,
        @NotNull(message = "Value cannot be null")
        @DecimalMin(value = "0.01", message = "Value must be greater than zero")
        BigDecimal value
) {}
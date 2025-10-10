package br.com.matheusgusmao.incometax.domain.model.expense;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public class DeductibleExpense {

    private Long id;
    private final String description;
    private final ExpenseType type;
    private final BigDecimal value;

    public DeductibleExpense(String description, ExpenseType type, BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense value must be positive.");
        }
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.value = value;
    }

    public DeductibleExpense(Long id, String description, ExpenseType type, BigDecimal value) {
        this(description, type, value);
        this.id = id;
    }
}

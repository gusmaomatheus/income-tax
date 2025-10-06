package br.com.matheusgusmao.incometax.domain.model.declaration;

import br.com.matheusgusmao.incometax.domain.model.income.Income;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Getter
public final class Declaration {

    private final Long id;
    private final UUID taxpayerId;
    private final int year;
    private final DeclarationStatus status;
    private final List<Income> incomes;

    private static final Pattern YEAR_REGEX_PATTERN = Pattern.compile("^\\d{4}$");

    public Declaration(UUID taxpayerId, int year) {
        Objects.requireNonNull(taxpayerId, "Taxpayer ID cannot be null.");

        if (!YEAR_REGEX_PATTERN.matcher(Integer.toString(year)).matches()) {
            throw new IllegalArgumentException("Invalid year format: " + year);
        }

        this.id = null;
        this.taxpayerId = taxpayerId;
        this.year = year;
        this.status = DeclarationStatus.EDITING;
        this.incomes = new ArrayList<>();
    }

    public Declaration(Long id, UUID taxpayerId, int year, DeclarationStatus status) {
        this.id = id;
        this.taxpayerId = taxpayerId;
        this.year = year;
        this.status = status;
        this.incomes = new ArrayList<>();

    }
    public void addIncome(Income income) {
        if (this.status != DeclarationStatus.EDITING) {
            throw new IllegalStateException("Cannot add income to a declaration that is not in editing status.");
        }
        this.incomes.add(income);
    }

    public void removeIncome(Long incomeId) {
        if (this.status != DeclarationStatus.EDITING) {
            throw new IllegalStateException("Cannot remove income from a declaration that is not in editing status.");
        }

        Income incomeToRemove = this.incomes.stream()
                .filter(income -> income.getId().equals(incomeId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Income not found with id: " + incomeId));

        this.incomes.remove(incomeToRemove);
    }
}
package br.com.matheusgusmao.incometax.domain.model.declaration;

import br.com.matheusgusmao.incometax.domain.model.dependent.Dependent;
import br.com.matheusgusmao.incometax.domain.model.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.domain.model.income.Income;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Getter
public final class Declaration {

    private final Long id;
    private final UUID taxpayerId;
    private final int year;
    private DeclarationStatus status;
    private LocalDateTime deliveryDate;
    private final List<Income> incomes;
    private final List<DeductibleExpense> deductibleExpenses = new ArrayList<>();
    private final List<Dependent> dependents = new ArrayList<>();

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

    public Declaration(Long id, UUID taxpayerId, int year, DeclarationStatus status, LocalDateTime deliveryDate) {
        this.id = id;
        this.taxpayerId = taxpayerId;
        this.year = year;
        this.status = status;
        this.incomes = new ArrayList<>();
        this.deliveryDate = deliveryDate;

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

    public List<DeductibleExpense> getDeductibleExpenses() {
        return Collections.unmodifiableList(deductibleExpenses);
    }

    public void addDeductibleExpense(DeductibleExpense expense) {
        if (this.status != DeclarationStatus.EDITING) {
            throw new IllegalStateException("Cannot add expense to a declaration that is not in editing status.");
        }
        this.deductibleExpenses.add(expense);
    }

    public void removeDeductibleExpense(Long expenseId) {
        if (this.status != DeclarationStatus.EDITING) {
            throw new IllegalStateException("Cannot remove expense from a declaration that is not in editing status.");
        }

        DeductibleExpense expenseToRemove = this.deductibleExpenses.stream()
                .filter(expense -> expense.getId().equals(expenseId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Deductible expense not found with id: " + expenseId));

        this.deductibleExpenses.remove(expenseToRemove);
    }

    public List<Dependent> getDependents() {
        return Collections.unmodifiableList(dependents);
    }

    public void addDependent(Dependent dependent) {
        if (this.status != DeclarationStatus.EDITING) {
            throw new IllegalStateException("Cannot add dependent to a declaration that is not in editing status.");
        }
        this.dependents.add(dependent);
    }

    public void removeDependent(Long dependentId) {
        if (this.status != DeclarationStatus.EDITING) {
            throw new IllegalStateException("Cannot remove dependent from a declaration that is not in editing status.");
        }

        Dependent dependentToRemove = this.dependents.stream()
                .filter(dependent -> dependent.getId().equals(dependentId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Dependent not found with id: " + dependentId));

        this.dependents.remove(dependentToRemove);
    }

    public void submit() {
        if (this.status != DeclarationStatus.EDITING) {
            throw new IllegalStateException("Declaration can only be submitted if it's in editing status.");
        }
        if (this.incomes.isEmpty()) {
            throw new IllegalStateException("Cannot submit a declaration with no incomes. Please report your incomes.");
        }
        this.status = DeclarationStatus.DELIVERED;
        this.deliveryDate = LocalDateTime.now();
    }

    public BigDecimal calculateTotalIncome() {
        return this.incomes.stream()
                .map(Income::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalDeductions() {
        return this.deductibleExpenses.stream()
                .map(DeductibleExpense::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
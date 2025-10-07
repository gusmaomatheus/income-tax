package br.com.matheusgusmao.incometax.infra.persistence.entity.declaration;

import br.com.matheusgusmao.incometax.domain.model.declaration.DeclarationStatus;
import br.com.matheusgusmao.incometax.infra.persistence.entity.expense.DeductibleExpenseEntity;
import br.com.matheusgusmao.incometax.infra.persistence.entity.income.IncomeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "declarations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"taxpayerId", "year"})
})
@Getter
@Setter
public class DeclarationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(Types.VARCHAR)
    @Column(nullable = false)
    private UUID taxpayerId;

    @Column(nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeclarationStatus status;

    @OneToMany(mappedBy = "declaration", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<IncomeEntity> incomes = new ArrayList<>();

    @OneToMany(mappedBy = "declaration", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DeductibleExpenseEntity> deductibleExpenses = new ArrayList<>();
}
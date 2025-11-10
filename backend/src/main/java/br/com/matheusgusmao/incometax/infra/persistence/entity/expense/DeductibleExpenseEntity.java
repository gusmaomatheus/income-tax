package br.com.matheusgusmao.incometax.infra.persistence.entity.expense;

import br.com.matheusgusmao.incometax.domain.model.expense.ExpenseType;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "deductible_expenses")
@Getter
@Setter
public class DeductibleExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ExpenseType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "declaration_id", nullable = false)
    private DeclarationEntity declaration;
}

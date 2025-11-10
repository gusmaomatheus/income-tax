package br.com.matheusgusmao.incometax.infra.persistence.entity.income;

import br.com.matheusgusmao.incometax.domain.model.income.IncomeType;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "incomes")
@Getter
@Setter
public class IncomeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String payingSource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IncomeType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "declaration_id", nullable = false)
    private DeclarationEntity declaration;
}
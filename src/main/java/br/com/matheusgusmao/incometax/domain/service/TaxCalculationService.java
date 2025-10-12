package br.com.matheusgusmao.incometax.domain.service;

import br.com.matheusgusmao.incometax.domain.model.declaration.TaxCalculationResult;
import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DeclarationMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TaxCalculationService {

    private final DeclarationRepository declarationRepository;
    private final DeclarationMapper declarationMapper;

    private static final BigDecimal FAIXA_1_LIMITE = new BigDecimal("24511.92");
    private static final BigDecimal FAIXA_2_LIMITE = new BigDecimal("33919.80");
    private static final BigDecimal FAIXA_2_ALIQUOTA = new BigDecimal("0.075");
    private static final BigDecimal FAIXA_2_DEDUCAO = new BigDecimal("1838.39");
    private static final BigDecimal FAIXA_3_LIMITE = new BigDecimal("45012.60");
    private static final BigDecimal FAIXA_3_ALIQUOTA = new BigDecimal("0.15");
    private static final BigDecimal FAIXA_3_DEDUCAO = new BigDecimal("4382.38");
    private static final BigDecimal FAIXA_4_LIMITE = new BigDecimal("55976.16");
    private static final BigDecimal FAIXA_4_ALIQUOTA = new BigDecimal("0.225");
    private static final BigDecimal FAIXA_4_DEDUCAO = new BigDecimal("7953.24");
    private static final BigDecimal FAIXA_5_ALIQUOTA = new BigDecimal("0.275");
    private static final BigDecimal FAIXA_5_DEDUCAO = new BigDecimal("10752.05");

    public TaxCalculationService(DeclarationRepository declarationRepository, DeclarationMapper declarationMapper) {
        this.declarationRepository = declarationRepository;
        this.declarationMapper = declarationMapper;
    }

    public TaxCalculationResult calculate(Long declarationId) {
        var declarationEntity = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration not found: " + declarationId));

        Declaration declaration = declarationMapper.toDomain(declarationEntity);

        BigDecimal totalIncome = declaration.calculateTotalIncome();
        BigDecimal totalDeductions = declaration.calculateTotalDeductions();
        BigDecimal calculationBase = totalIncome.subtract(totalDeductions);

        if (calculationBase.compareTo(BigDecimal.ZERO) <= 0) {
            return new TaxCalculationResult(totalIncome, totalDeductions, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        BigDecimal taxDue = calculateProgressiveTax(calculationBase);
        BigDecimal effectiveAliquot = taxDue.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

        return new TaxCalculationResult(totalIncome, totalDeductions, calculationBase, taxDue, effectiveAliquot);
    }

    private BigDecimal calculateProgressiveTax(BigDecimal base) {
        BigDecimal tax;
        if (base.compareTo(FAIXA_1_LIMITE) <= 0) {
            tax = BigDecimal.ZERO;
        } else if (base.compareTo(FAIXA_2_LIMITE) <= 0) {
            tax = base.multiply(FAIXA_2_ALIQUOTA).subtract(FAIXA_2_DEDUCAO);
        } else if (base.compareTo(FAIXA_3_LIMITE) <= 0) {
            tax = base.multiply(FAIXA_3_ALIQUOTA).subtract(FAIXA_3_DEDUCAO);
        } else if (base.compareTo(FAIXA_4_LIMITE) <= 0) {
            tax = base.multiply(FAIXA_4_ALIQUOTA).subtract(FAIXA_4_DEDUCAO);
        } else {
            tax = base.multiply(FAIXA_5_ALIQUOTA).subtract(FAIXA_5_DEDUCAO);
        }
        return tax.setScale(2, RoundingMode.HALF_UP);
    }
}
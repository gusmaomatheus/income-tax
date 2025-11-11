**Linha:** 58

**Mutante(ID):** CONDITIONALS_BOUNDARY

**Justificativa de Equivalência:** O mutante troca o operador <= por < na verificação da Faixa 1 de imposto (if (base.compareTo(FAIXA_1_LIMITE) <= 0)). O teste de unidade (testAtFaixa1Boundary) usa um valor-limite exato (ex: 24511.92).

Código Original (<=): A condição é true, o imposto é 0, e o setScale(2) (linha 60) retorna 0.00.

Código Mutante (<): A condição é false. O código pula para a Faixa 2, onde o cálculo (24511.92 * 0.075) - 1838.39 resulta em 0.004. Este valor, ao passar pelo setScale(2) (linha 70), é arredondado para 0.00.

Ambos os caminhos resultam em 0.00, fazendo com que o teste passe.

-------------------------------------------------------------------------------------------------

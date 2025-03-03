package com.app.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SaqueDepositoVO {

    private final Long id;

    private final BigDecimal valor;

    public BigDecimal getValorSaqueComTxAdmin(BigDecimal valor, boolean exclusive){
        if(!exclusive){
            // Acrescentar o valor da taxa administração mediante regra de negócio.
            return this.calcularTaxaAdministracao(this.valor);
        } else {
            // Isento de Taxas quando for plano exclusive.
            return valor;
        }
    }

    private BigDecimal calcularTaxaAdministracao(BigDecimal valorSaque){

        BigDecimal valorComTaxa = BigDecimal.ZERO;
        var compareTo100 = valorSaque.compareTo(BigDecimal.valueOf(100));
        var compareTo300 = valorSaque.compareTo(BigDecimal.valueOf(300));
        if((compareTo100 == 1) && (compareTo300 == 0 || compareTo300 == -1)){
            valorComTaxa = valorSaque.multiply(BigDecimal.valueOf(0.04)).add(valorSaque);
        } else if(compareTo300 == 1){
            valorComTaxa = valorSaque.multiply(BigDecimal.valueOf(0.1)).add(valorSaque);
        }
        return valorComTaxa;
    }
}

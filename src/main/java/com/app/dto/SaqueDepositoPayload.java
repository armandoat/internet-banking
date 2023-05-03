package com.app.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class SaqueDepositoPayload {

    @NotNull
    private Long id;

    @NotNull
    private BigDecimal valor;
}

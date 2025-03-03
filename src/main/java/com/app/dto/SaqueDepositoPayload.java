package com.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SaqueDepositoPayload {

    @NotNull
    private Long id;

    @NotNull
    private BigDecimal valor;
}

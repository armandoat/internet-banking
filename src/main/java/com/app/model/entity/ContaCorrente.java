package com.app.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaCorrente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String numeroConta;
    private Boolean exclusive;
    private BigDecimal saldo;
}
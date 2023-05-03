package com.app.service;

import com.app.dto.SaqueDepositoPayload;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface ContaCorrenteService {

    ResponseEntity sacarValorConta(SaqueDepositoPayload contaCorrentePayload);

    ResponseEntity depositarValorConta(SaqueDepositoPayload contaCorrentePayload);

    ResponseEntity buscarHistoricoTransacaoByDataTransacao(LocalDate dataTransacao, Pageable pageable);
}

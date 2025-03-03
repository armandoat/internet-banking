package com.app.service;

import com.app.model.entity.ContaCorrente;
import com.app.vo.SaqueDepositoVO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface TransacaoContaCorrenteService {

    ResponseEntity<ContaCorrente> sacarValorConta(SaqueDepositoVO vo);

    ResponseEntity<ContaCorrente> depositarValorConta(SaqueDepositoVO vo);

    ResponseEntity buscarHistoricoTransacaoByDataTransacao(LocalDate dataTransacao, Pageable pageable);
}

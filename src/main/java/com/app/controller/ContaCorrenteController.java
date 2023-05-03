package com.app.controller;

import com.app.dto.SaqueDepositoPayload;
import com.app.service.ContaCorrenteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/contaCorrente")
@RequiredArgsConstructor
public class ContaCorrenteController {

    private static final Logger logger = LoggerFactory.getLogger(ContaCorrenteController.class);

    private final ContaCorrenteService contaCorrenteService;

    @GetMapping("/{dataTransacao}")
    public ResponseEntity consultarHistoricoTransacoes(@PathVariable LocalDate dataTransacao, Pageable pageable){
        logger.debug("Retornar o histórico de transações de cada movimentação para a data: {}.", dataTransacao);
        return contaCorrenteService.buscarHistoricoTransacaoByDataTransacao(dataTransacao, pageable);
    }

    @PostMapping
    public ResponseEntity realizarDeposito(@RequestBody @Valid SaqueDepositoPayload contaCorrentePayload){
        logger.debug("Realizar o depósito na conta corrente correpondente ao id: {}.", contaCorrentePayload.getId());
        return contaCorrenteService.depositarValorConta(contaCorrentePayload);
    }

    @PutMapping("/")
    public ResponseEntity realizarSaque(@RequestBody @Valid SaqueDepositoPayload contaCorrentePayload){
        logger.debug("Realizar o saque na conta corrente correpondente ao id: {}.", contaCorrentePayload.getId());
        return contaCorrenteService.sacarValorConta(contaCorrentePayload);
    }
}

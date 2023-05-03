package com.app.service;

import com.app.dto.SaqueDepositoPayload;
import com.app.model.entity.ContaCorrente;
import com.app.model.entity.HistoricoTransacao;
import com.app.repository.ContaCorrenteRepository;
import com.app.repository.HistoricoTransacaoRepository;
import com.app.util.model.TipoTransacaoEnum;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ContaCorrenteServiceImpl implements ContaCorrenteService{

    private static final Logger logger = LoggerFactory.getLogger(ContaCorrenteServiceImpl.class);

    private final ContaCorrenteRepository contaCorrenteRepository;
    private final HistoricoTransacaoRepository historicoTransacaoRepository;

    @Override
    public ResponseEntity sacarValorConta(SaqueDepositoPayload contaCorrentePayload) {


        Optional<ContaCorrente> optional = contaCorrenteRepository.findByIdAndSaldoGreaterThanEqual(contaCorrentePayload.getId(), contaCorrentePayload.getValor());

        if(optional.isPresent()){
            ContaCorrente contaCorrente = optional.get();
            BigDecimal valorSacado = BigDecimal.ZERO;
            if(!contaCorrente.getExclusive()){
                // Acrescenta o valor da taxa administrativa mediante regra de negócio.
                valorSacado = this.calcularTaxaAdministracao(contaCorrentePayload.getValor());
            } else {
                // Isento de Taxas quando for plano exclusive.
                valorSacado = contaCorrentePayload.getValor();
            }
            contaCorrente.setSaldo(contaCorrente.getSaldo().subtract(valorSacado));
            // Atualiza a Conta Corrente
            logger.debug("Atualizando o saldo na conta corrente: {} após o saque.", contaCorrente.getNumeroConta());
            contaCorrenteRepository.saveAndFlush(contaCorrente);
            // Grava o histórico de transações da Conta Corrente.
            logger.debug("Gravando a operação de saque na Conta Corrente: {} no histórico de transações.", contaCorrente.getNumeroConta());
            this.gravarHistoricoTransacao(TipoTransacaoEnum.SAQUE, valorSacado, contaCorrente);
            //
            return new ResponseEntity(contaCorrente, HttpStatus.OK);
        }
        //
        return new ResponseEntity(contaCorrentePayload, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity depositarValorConta(SaqueDepositoPayload contaCorrentePayload) {
        Optional<ContaCorrente> optional = contaCorrenteRepository.findById(contaCorrentePayload.getId());
        if(!optional.isPresent()){
            return new ResponseEntity(contaCorrentePayload, HttpStatus.NOT_FOUND);
        }
        ContaCorrente contaCorrente = optional.get();
        contaCorrente.setSaldo(contaCorrente.getSaldo().add(contaCorrentePayload.getValor()));
        // Atualiza a Conta Corrente
        logger.debug("Atualizando o saldo na Conta Corrente: {} após o depósito.", contaCorrente.getNumeroConta());
        contaCorrenteRepository.save(contaCorrente);
        // Grava o histórico de transações da Conta Corrente
        logger.debug("Gravando a operação de depósito na Conta Corrente: {} no histórico de transações.", contaCorrente.getNumeroConta());
        this.gravarHistoricoTransacao(TipoTransacaoEnum.DEPOSITO, contaCorrentePayload.getValor(), contaCorrente);
        return new ResponseEntity(contaCorrente, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity buscarHistoricoTransacaoByDataTransacao(LocalDate dataTransacao, Pageable pageable) {
        List<HistoricoTransacao> historicoTransacoes = historicoTransacaoRepository.findAllByDataTransacao(dataTransacao, pageable);
        if(historicoTransacoes.isEmpty()){
            logger.debug("Não foi encontrado histórico de transações para a data: {}.", dataTransacao);
            return new ResponseEntity(dataTransacao, HttpStatus.NOT_FOUND);
        }
        // Devolve uma lista de histórico de transação ordenada pelo campo número da conta corrente
        Comparator<HistoricoTransacao> comparator = Comparator.comparing(h -> h.getContaCorrente().getNumeroConta());
        logger.debug("Retornando o histórico de transação de cada movimentação para a data: {}.", dataTransacao);
        return new ResponseEntity(historicoTransacoes.stream().sorted(comparator).collect(Collectors.toList()), HttpStatus.OK);
    }

    protected void gravarHistoricoTransacao(TipoTransacaoEnum tipoTransacao, BigDecimal valor, ContaCorrente contaCorrente){
        HistoricoTransacao historicoTransacao = new HistoricoTransacao();
        historicoTransacao.setTipoTransacao(tipoTransacao);
        historicoTransacao.setValor(valor);
        historicoTransacao.setDataTransacao(LocalDate.now());
        historicoTransacao.setContaCorrente(contaCorrente);
        historicoTransacaoRepository.save(historicoTransacao);
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
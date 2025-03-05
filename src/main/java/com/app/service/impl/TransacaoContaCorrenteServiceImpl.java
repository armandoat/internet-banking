package com.app.service.impl;

import com.app.model.entity.ContaCorrente;
import com.app.model.entity.HistoricoTransacao;
import com.app.repository.ContaCorrenteRepository;
import com.app.repository.HistoricoTransacaoRepository;
import com.app.service.TransacaoContaCorrenteService;
import com.app.util.model.TipoTransacaoEnum;
import com.app.vo.SaqueDepositoVO;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TransacaoContaCorrenteServiceImpl implements TransacaoContaCorrenteService {

    private final ContaCorrenteRepository contaCorrenteRepository;
    private final HistoricoTransacaoRepository historicoTransacaoRepository;

    @Override
    public ResponseEntity<ContaCorrente> sacarValorConta(SaqueDepositoVO vo) {
        ContaCorrente contaCorrente = contaCorrenteRepository.findByIdAndSaldoGreaterThanEqual(vo.getId(), vo.getValor());
        Optional.ofNullable(contaCorrente).orElseThrow(() -> new ValidationException("Conta inválida ou Saldo insuficiente!"));
        // Atualizar saldo com a taxa de administração antes de setar o saldo na Conta Corrente
        BigDecimal valorSaqueComTxAdmin = vo.getValorSaqueComTxAdmin(vo.getValor(), contaCorrente.getExclusive());
        contaCorrente.setSaldo(contaCorrente.getSaldo().subtract(valorSaqueComTxAdmin));
        // Grava o histórico de transações da Conta Corrente.
        log.debug("Gravando a operação de saque na Conta Corrente: {} no histórico de transações.", contaCorrente.getNumeroConta());
        this.gravarHistoricoTransacao(TipoTransacaoEnum.SAQUE, valorSaqueComTxAdmin, contaCorrente);
        //
        return new ResponseEntity<>(contaCorrente, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ContaCorrente> depositarValorConta(SaqueDepositoVO vo) {
        Optional<ContaCorrente> optional = contaCorrenteRepository.findById(vo.getId());
        optional.orElseThrow(() -> new ResourceNotFoundException("Conta inválida!"));
        ContaCorrente contaCorrente = optional.get();
        contaCorrente.setSaldo(contaCorrente.getSaldo().add(vo.getValor()));
        // Grava o histórico de transações da Conta Corrente
        log.debug("Gravando a operação de depósito na Conta Corrente: {} no histórico de transações.", contaCorrente.getNumeroConta());
        this.gravarHistoricoTransacao(TipoTransacaoEnum.DEPOSITO, vo.getValor(), contaCorrente);
        return new ResponseEntity<>(contaCorrente, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity buscarHistoricoTransacaoByDataTransacao(LocalDate dataTransacao, Pageable pageable) {
        List<HistoricoTransacao> historicoTransacoes = historicoTransacaoRepository.findAllByDataTransacao(dataTransacao, pageable);
        if(CollectionUtils.isEmpty(historicoTransacoes)){
            log.debug("Não foi encontrado histórico de transações para a data: {}.", dataTransacao);
            throw new ResourceNotFoundException("Não foi encontrado histórico de transações para a data: " + dataTransacao + "!");
        }
        // Devolve uma lista de histórico de transação ordenada pelo campo número da conta corrente
        Comparator<HistoricoTransacao> comparator = Comparator.comparing(h -> h.getContaCorrente().getNumeroConta());
        log.debug("Retornando o histórico de transação de cada movimentação para a data: {}.", dataTransacao);
        return new ResponseEntity<>(historicoTransacoes.stream().sorted(comparator).collect(Collectors.toList()), HttpStatus.OK);
    }

    protected void gravarHistoricoTransacao(TipoTransacaoEnum tipoTransacao, BigDecimal valor, ContaCorrente contaCorrente){
        HistoricoTransacao historicoTransacao = new HistoricoTransacao();
        historicoTransacao.setTipoTransacao(tipoTransacao);
        historicoTransacao.setValor(valor);
        historicoTransacao.setDataTransacao(LocalDate.now());
        historicoTransacao.setContaCorrente(contaCorrente);
        historicoTransacaoRepository.save(historicoTransacao);
    }
}
package com.app.service;

import com.app.dto.SaqueDepositoPayload;
import com.app.model.entity.ContaCorrente;
import com.app.model.entity.HistoricoTransacao;
import com.app.repository.ContaCorrenteRepository;
import com.app.repository.HistoricoTransacaoRepository;
import com.app.util.model.TipoTransacaoEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContaCorrenteServiceImplTest {

    @InjectMocks
    private ContaCorrenteServiceImpl contaCorrenteService;

    @Mock
    private ContaCorrenteRepository contaCorrenteRepository;

    @Mock
    private HistoricoTransacaoRepository historicoTransacaoRepository;

    private ContaCorrente contaCorrente;

    @BeforeEach
    void init(){
        contaCorrente = getContaCorrenteEntity();
    }

    @Test
    void  deveSacarValorContaPlanoExclusive(){

        when(contaCorrenteRepository.findByIdAndSaldoGreaterThanEqual(anyLong(), any())).thenReturn(Optional.of(contaCorrente));

        ResponseEntity response = contaCorrenteService.sacarValorConta(getSaqueDepositoPayload());

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertTrue(((ContaCorrente) response.getBody()).getExclusive());
    }

    @Test
    void  deveRetornarStatusNotFoundAoSacarValorConta(){

        ContaCorrente contaCorrente = getContaCorrenteEntity();
        when(contaCorrenteRepository.findByIdAndSaldoGreaterThanEqual(anyLong(), any())).thenReturn(Optional.empty());

        ResponseEntity response = contaCorrenteService.sacarValorConta(getSaqueDepositoPayload());

        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void  deveSacarValorContaSemTaxaPlanoNaoExclusive(){

        contaCorrente.setExclusive(Boolean.FALSE);
        when(contaCorrenteRepository.findByIdAndSaldoGreaterThanEqual(anyLong(), any())).thenReturn(Optional.of(contaCorrente));

        SaqueDepositoPayload payload = getSaqueDepositoPayload();
        ResponseEntity response = contaCorrenteService.sacarValorConta(payload);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

        ContaCorrente body = (ContaCorrente) response.getBody();
        assertFalse(body.getExclusive());
    }

    @Test
    void  deveSacarValorContaComTaxaZeroPontoQuatro(){

        contaCorrente.setExclusive(Boolean.FALSE);
        when(contaCorrenteRepository.findByIdAndSaldoGreaterThanEqual(anyLong(), any())).thenReturn(Optional.of(contaCorrente));

        SaqueDepositoPayload payload = getSaqueDepositoPayload();
        payload.setValor(BigDecimal.valueOf(101));
        ResponseEntity response = contaCorrenteService.sacarValorConta(payload);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

        ContaCorrente body = (ContaCorrente) response.getBody();
        assertFalse(body.getExclusive());
    }

    @Test
    void  deveSacarValorContaComTaxaUmPorCento(){

        contaCorrente.setExclusive(Boolean.FALSE);
        when(contaCorrenteRepository.findByIdAndSaldoGreaterThanEqual(anyLong(), any())).thenReturn(Optional.of(contaCorrente));

        SaqueDepositoPayload payload = getSaqueDepositoPayload();
        payload.setValor(BigDecimal.valueOf(301));
        ResponseEntity response = contaCorrenteService.sacarValorConta(payload);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

        ContaCorrente body = (ContaCorrente) response.getBody();
        assertFalse(body.getExclusive());
    }

    @Test
    void deveDepositarValorConta(){

        when(contaCorrenteRepository.findById(anyLong())).thenReturn(Optional.of(contaCorrente));

        ResponseEntity response = contaCorrenteService.depositarValorConta(getSaqueDepositoPayload());

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    void  deveRetornarStatusNotFoundAoDepositarValorConta(){

        ContaCorrente contaCorrente = getContaCorrenteEntity();
        when(contaCorrenteRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity response = contaCorrenteService.depositarValorConta(getSaqueDepositoPayload());

        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void deveBuscarHistoricoTransacaoByDataTransacao(){

        HistoricoTransacao historicoTransacao = getHistoricoTransacaoEntity();
        PageRequest paginacao = PageRequest.of(1, 10);

        when(historicoTransacaoRepository.findAllByDataTransacao(LocalDate.now(), paginacao)).thenReturn(List.of(historicoTransacao));

        ResponseEntity response = contaCorrenteService.buscarHistoricoTransacaoByDataTransacao(LocalDate.now(), paginacao);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void deveRetornarStatusNotFoundAoBuscarHistoricoTransacao(){

        HistoricoTransacao historicoTransacao = getHistoricoTransacaoEntity();
        PageRequest paginacao = PageRequest.of(1, 10);

        when(historicoTransacaoRepository.findAllByDataTransacao(LocalDate.now(), paginacao)).thenReturn(List.of());

        ResponseEntity response = contaCorrenteService.buscarHistoricoTransacaoByDataTransacao(LocalDate.now(), paginacao);

        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    private ContaCorrente getContaCorrenteEntity(){
        return ContaCorrente.builder()
                .id(1L)
                .numeroConta("111")
                .saldo(BigDecimal.valueOf(100))
                .exclusive(Boolean.TRUE)
                .build();
    }

    private SaqueDepositoPayload getSaqueDepositoPayload(){
        SaqueDepositoPayload payload = new SaqueDepositoPayload();
        payload.setId(1L);
        payload.setValor(BigDecimal.TEN);
        return payload;
    }

    private HistoricoTransacao getHistoricoTransacaoEntity(){
        return HistoricoTransacao.builder()
                .id(1L)
                .dataTransacao(LocalDate.now())
                .tipoTransacao(TipoTransacaoEnum.SAQUE)
                .contaCorrente(getContaCorrenteEntity())
                .valor(BigDecimal.TEN)
                .build();
    }
}

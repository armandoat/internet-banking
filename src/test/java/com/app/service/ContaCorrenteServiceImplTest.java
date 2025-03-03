package com.app.service;

import com.app.model.entity.ContaCorrente;
import com.app.model.entity.HistoricoTransacao;
import com.app.repository.ContaCorrenteRepository;
import com.app.repository.HistoricoTransacaoRepository;
import com.app.service.impl.TransacaoContaCorrenteServiceImpl;
import com.app.util.model.TipoTransacaoEnum;
import com.app.vo.SaqueDepositoVO;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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
    private TransacaoContaCorrenteServiceImpl contaCorrenteService;

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

        when(contaCorrenteRepository.findByIdAndSaldoGreaterThanEqual(anyLong(), any())).thenReturn(contaCorrente);

        ResponseEntity<ContaCorrente> response = contaCorrenteService.sacarValorConta(getSaqueDepositoVO());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void  deveRetornarStatusNotFoundAoSacarValorConta(){

        when(contaCorrenteRepository.findByIdAndSaldoGreaterThanEqual(anyLong(), any())).thenReturn(null);

        ValidationException validationException = assertThrows(ValidationException.class, () ->
                contaCorrenteService.sacarValorConta(getSaqueDepositoVO()));

        assertEquals("Conta inválida ou Saldo insuficiente!", validationException.getMessage());
    }

    @Test
    void  deveSacarValorContaSemTaxaPlanoNaoExclusive(){

        contaCorrente.setExclusive(Boolean.FALSE);
        when(contaCorrenteRepository.findByIdAndSaldoGreaterThanEqual(anyLong(), any())).thenReturn(contaCorrente);

        ResponseEntity<ContaCorrente> response = contaCorrenteService.sacarValorConta(getSaqueDepositoVO());

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ContaCorrente body = response.getBody();
        assert body != null;
        assertFalse(body.getExclusive());
    }

    @Test
    void  deveSacarValorContaComTaxaZeroPontoQuatro(){

        contaCorrente.setExclusive(Boolean.FALSE);
        when(contaCorrenteRepository.findByIdAndSaldoGreaterThanEqual(anyLong(), any())).thenReturn(contaCorrente);

        SaqueDepositoVO payload = getSaqueDepositoVO();
        ResponseEntity<ContaCorrente> response = contaCorrenteService.sacarValorConta(payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void  deveSacarValorContaComTaxaUmPorCento(){

        contaCorrente.setExclusive(Boolean.FALSE);
        when(contaCorrenteRepository.findByIdAndSaldoGreaterThanEqual(anyLong(), any())).thenReturn(contaCorrente);

        SaqueDepositoVO vo = new SaqueDepositoVO(1L, BigDecimal.valueOf(301));
        ResponseEntity<ContaCorrente> response = contaCorrenteService.sacarValorConta(vo);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ContaCorrente body = response.getBody();
        assert body != null;
        assertFalse(body.getExclusive());
    }

    @Test
    void deveDepositarValorConta(){

        when(contaCorrenteRepository.findById(anyLong())).thenReturn(Optional.of(contaCorrente));

        ResponseEntity<ContaCorrente> response = contaCorrenteService.depositarValorConta(getSaqueDepositoVO());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void  deveRetornarStatusNotFoundAoDepositarValorConta(){

        when(contaCorrenteRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> contaCorrenteService.depositarValorConta(getSaqueDepositoVO()));

        assertEquals("Conta inválida!", resourceNotFoundException.getMessage());
    }

    @Test
    void deveBuscarHistoricoTransacaoByDataTransacao(){

        HistoricoTransacao historicoTransacao = getHistoricoTransacaoEntity();
        PageRequest paginacao = PageRequest.of(1, 10);

        when(historicoTransacaoRepository.findAllByDataTransacao(LocalDate.now(), paginacao)).thenReturn(List.of(historicoTransacao));

        ResponseEntity response = contaCorrenteService.buscarHistoricoTransacaoByDataTransacao(LocalDate.now(), paginacao);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deveRetornarStatusNotFoundAoBuscarHistoricoTransacao(){

        PageRequest paginacao = PageRequest.of(1, 10);

        when(historicoTransacaoRepository.findAllByDataTransacao(LocalDate.now(), paginacao)).thenReturn(List.of());

        LocalDate now = LocalDate.now();

        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> contaCorrenteService.buscarHistoricoTransacaoByDataTransacao(now, paginacao));

        assertEquals("Não foi encontrado histórico de transações para a data: " + now + "!", resourceNotFoundException.getMessage());
    }

    private ContaCorrente getContaCorrenteEntity(){
        return ContaCorrente.builder()
                .id(1L)
                .numeroConta("111")
                .saldo(BigDecimal.valueOf(100))
                .exclusive(Boolean.TRUE)
                .build();
    }

    private SaqueDepositoVO getSaqueDepositoVO(){
        return new SaqueDepositoVO(1L, BigDecimal.TEN);
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

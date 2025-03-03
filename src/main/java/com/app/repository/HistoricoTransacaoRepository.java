package com.app.repository;

import com.app.model.entity.HistoricoTransacao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface HistoricoTransacaoRepository extends JpaRepository<HistoricoTransacao, Long> {

    List<HistoricoTransacao> findAllByDataTransacao(LocalDate dataTransacao, Pageable pageable);
}
package com.app.repository;

import com.app.model.entity.ContaCorrente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, Long> {

    Optional<ContaCorrente> findByIdAndSaldoGreaterThanEqual(Long id, BigDecimal valor);
}
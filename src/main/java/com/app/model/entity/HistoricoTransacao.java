package com.app.model.entity;

import com.app.util.model.TipoTransacaoEnum;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoTransacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Enumerated(EnumType.STRING)
    private TipoTransacaoEnum tipoTransacao;
    private BigDecimal  valor;
    private LocalDate dataTransacao;

    @ManyToOne
    private ContaCorrente contaCorrente;
}

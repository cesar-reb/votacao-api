package com.cesar.votacao.api.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"pauta_id", "associadoId"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cpfAssociado;

    @Enumerated(EnumType.STRING)
    private OpcaoVoto opcao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sessao_id")
    private SessaoVotacao sessao;

    public Voto(String cpfAssociado, OpcaoVoto opcao, SessaoVotacao sessao) {
        this.cpfAssociado = cpfAssociado;
        this.opcao = opcao;
        this.sessao = sessao;
    }

    public enum OpcaoVoto {
        SIM, NAO
    }
}

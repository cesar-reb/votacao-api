package com.cesar.votacao.api.domain.model;

import com.cesar.votacao.api.domain.exception.SessaoJaAbertaException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pauta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String titulo;

    @Setter
    private String descricao;

    private LocalDateTime dataCriacao;

    private UUID usuarioCriadorId;

    @OneToOne(mappedBy = "pauta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SessaoVotacao sessao;

    public Pauta(String titulo, String descricao, UUID usuarioCriadorId) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataCriacao = LocalDateTime.now();
        this.usuarioCriadorId = usuarioCriadorId;
    }

    public void abrirSessao(long minutosDuracao) {
        if (this.sessao != null) {
            throw new SessaoJaAbertaException(this.id);
        }
        this.sessao = new SessaoVotacao(this, minutosDuracao);
    }

    public boolean sessaoEstaAtiva() {
        return sessao != null && sessao.estaAberta();
    }

}

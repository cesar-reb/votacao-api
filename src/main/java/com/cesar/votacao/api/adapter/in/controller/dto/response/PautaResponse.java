package com.cesar.votacao.api.adapter.in.controller.dto.response;

import com.cesar.votacao.api.domain.model.Pauta;
import com.cesar.votacao.api.domain.model.SessaoVotacao;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record PautaResponse (Long id, String titulo,
                             String descricao, LocalDateTime dataCriacao,
                             SessaoVotacao sessao){

    public static PautaResponse fromPauta(Pauta pauta){
        return PautaResponse.builder()
                .id(pauta.getId())
                .titulo(pauta.getTitulo())
                .descricao(pauta.getDescricao())
                .dataCriacao(pauta.getDataCriacao())
                .sessao(pauta.getSessao())
                .build();
    }
}

package com.cesar.votacao.api.port.in;

import com.cesar.votacao.api.domain.model.Pauta;

import java.util.UUID;

public interface PautaService {
    Pauta criarPauta(String titulo, String descricao, UUID usuarioCriadorId);
    void abrirSessao(Long pautaId, long duracaoEmMinutos);
}

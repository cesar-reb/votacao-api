package com.cesar.votacao.api.port.in;

import com.cesar.votacao.api.domain.model.ResultadoVotacao;
import com.cesar.votacao.api.domain.model.Voto;

public interface VotacaoUseCase {
    void votar(String cpf, Long pautaId, Voto.OpcaoVoto opcao);
    ResultadoVotacao obterResultado(Long pautaId);
}

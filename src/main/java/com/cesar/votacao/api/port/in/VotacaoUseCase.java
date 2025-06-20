package com.cesar.votacao.api.port.in;

import com.cesar.votacao.api.domain.model.ResultadoVotacao;

public interface VotacaoUseCase {
    void votar(String cpf, Long pautaId, String opcao);
    ResultadoVotacao obterResultado(Long pautaId);
}

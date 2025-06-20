package com.cesar.votacao.api.domain.exception;

public class SessaoJaAbertaException extends RuntimeException {

    public SessaoJaAbertaException(Long pautaId) {
        super("A sessão de votação já está aberta para a pauta com ID " + pautaId);
    }
}

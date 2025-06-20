package com.cesar.votacao.api.port.out;

public interface ValidadorCPF {
    boolean podeVotar(String cpf);
}

package com.cesar.votacao.api.adapter.out.external;

import com.cesar.votacao.api.adapter.out.external.http.ValidadorCPFClient;
import com.cesar.votacao.api.port.out.ValidadorCPF;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ValidadorCPFImpl implements ValidadorCPF {

    private final ValidadorCPFClient validadorCPFClient;

    @Override
    public boolean podeVotar(String cpf) {

        var statusResponse = validadorCPFClient.consultarCpf(cpf);

        return "ABLE_TO_VOTE".equalsIgnoreCase(statusResponse.status());
    }
}

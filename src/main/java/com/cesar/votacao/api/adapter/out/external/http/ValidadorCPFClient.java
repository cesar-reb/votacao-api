package com.cesar.votacao.api.adapter.out.external.http;

import com.cesar.votacao.api.adapter.out.external.dto.CpfStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "validador-cpf", url = "${validador-cpf.url}")
public interface ValidadorCPFClient {

    @GetMapping("/{cpf}")
    CpfStatusResponse consultarCpf(@PathVariable("cpf") String cpf);
}

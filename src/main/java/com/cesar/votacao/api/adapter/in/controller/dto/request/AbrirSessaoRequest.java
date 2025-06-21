package com.cesar.votacao.api.adapter.in.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AbrirSessaoRequest(@NotBlank Long duracaoEmMinutos) {
}

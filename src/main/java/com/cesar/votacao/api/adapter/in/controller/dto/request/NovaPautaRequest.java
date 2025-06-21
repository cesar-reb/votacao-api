package com.cesar.votacao.api.adapter.in.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NovaPautaRequest(@NotBlank String titulo, @NotBlank String descricao) {
}

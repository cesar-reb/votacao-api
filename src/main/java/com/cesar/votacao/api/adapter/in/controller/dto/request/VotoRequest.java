package com.cesar.votacao.api.adapter.in.controller.dto.request;

import com.cesar.votacao.api.domain.model.Voto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VotoRequest(
        @NotBlank String cpfAssociado,
        @NotNull Voto.OpcaoVoto voto
) {}
package com.cesar.votacao.api.adapter.in.controller.v1;

import com.cesar.votacao.api.adapter.in.controller.dto.request.AbrirSessaoRequest;
import com.cesar.votacao.api.adapter.in.controller.dto.request.NovaPautaRequest;
import com.cesar.votacao.api.adapter.in.controller.dto.request.VotoRequest;
import com.cesar.votacao.api.adapter.in.controller.dto.response.PautaResponse;
import com.cesar.votacao.api.domain.model.ResultadoVotacao;
import com.cesar.votacao.api.port.in.PautaService;
import com.cesar.votacao.api.port.in.VotacaoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/pautas")
@RequiredArgsConstructor
public class PautaController {

    private final PautaService pautaService;
    private final VotacaoUseCase votacaoUseCase;

    @PostMapping
    public ResponseEntity<PautaResponse> criarPauta(@RequestBody @Valid NovaPautaRequest request,
                                                    @RequestHeader UUID userId) {
        var pauta = pautaService.criarPauta(request.titulo(), request.descricao(), userId);
        return ResponseEntity.ok(PautaResponse.fromPauta(pauta));
    }

    @PostMapping("/{id}/sessao")
    public ResponseEntity<Void> abrirSessao(@PathVariable Long id, @RequestBody(required = false) AbrirSessaoRequest request) {
        pautaService.abrirSessao(id, request.duracaoEmMinutos());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/voto")
    public ResponseEntity<Void> votar(@RequestBody @Valid VotoRequest request, @PathVariable Long id) {
        votacaoUseCase.votar(
                request.cpfAssociado(),
                id,
                request.voto()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/resultado")
    public ResponseEntity<ResultadoVotacao> resultado(@PathVariable Long id) {
        var resultado = votacaoUseCase.obterResultado(id);
        return ResponseEntity.ok(resultado);
    }

}

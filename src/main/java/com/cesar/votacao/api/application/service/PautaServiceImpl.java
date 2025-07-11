package com.cesar.votacao.api.application.service;

import com.cesar.votacao.api.domain.model.Pauta;
import com.cesar.votacao.api.port.in.PautaService;
import com.cesar.votacao.api.port.out.PautaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PautaServiceImpl implements PautaService {

    private final PautaRepository pautaRepository;

    @Override
    @Transactional
    public Pauta criarPauta(String titulo, String descricao, UUID usuarioCriadorId) {
        log.info("criando pauta");
        var pauta = new Pauta(titulo, descricao, usuarioCriadorId);
        return pautaRepository.save(pauta);
    }

    @Override
    @Transactional
    public void abrirSessao(Long pautaId, Long duracaoEmMinutos) {

        log.info("abrindo sessão");
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new IllegalArgumentException("Pauta não encontrada com ID: " + pautaId));

        var duracao = duracaoEmMinutos != null ? duracaoEmMinutos : 1;
        pauta.abrirSessao(duracao);
        pautaRepository.save(pauta);
    }
}

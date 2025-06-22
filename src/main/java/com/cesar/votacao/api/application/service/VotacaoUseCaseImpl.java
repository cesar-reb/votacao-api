package com.cesar.votacao.api.application.service;

import com.cesar.votacao.api.port.dto.ContagemVotoProjecao;
import com.cesar.votacao.api.domain.model.ResultadoVotacao;
import com.cesar.votacao.api.domain.model.SessaoVotacao;
import com.cesar.votacao.api.domain.model.Voto;
import com.cesar.votacao.api.port.in.VotacaoUseCase;
import com.cesar.votacao.api.port.out.PautaRepository;
import com.cesar.votacao.api.port.out.ValidadorCPF;
import com.cesar.votacao.api.port.out.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VotacaoUseCaseImpl implements VotacaoUseCase {

    private final PautaRepository pautaRepository;
    private final VotoRepository votoRepository;
    private final ValidadorCPF validadorCPF;

    @Override
    @Transactional
    public void votar(String cpfAssociado, Long pautaId, Voto.OpcaoVoto voto) {

        var pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new IllegalArgumentException("Pauta não encontrada com ID: " + pautaId));

        var sessao = pauta.getSessao();
        assegurarSessaoAtiva(sessao);
        assegurarVotoUnicoPorSessao(cpfAssociado, sessao);
        assegurarVotoValido(cpfAssociado);

        var votoEntity = new Voto(cpfAssociado, voto, sessao);
        votoRepository.save(votoEntity);
    }

    @Override
    public ResultadoVotacao obterResultado(Long pautaId) {
        var pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new IllegalArgumentException("Pauta não encontrada com ID: " + pautaId));

        var sessao = pauta.getSessao();
        assegurarPautaComSecao(sessao);

        var resultados = votoRepository.contarVotosPorSessao(sessao.getId())
                .stream()
                .collect(Collectors.toMap(
                        resultado -> resultado.getOpcao().toUpperCase(),
                        ContagemVotoProjecao::getTotal
                ));

        int sim = resultados.getOrDefault("SIM", 0);
        int nao = resultados.getOrDefault("NAO", 0);

        return new ResultadoVotacao(sim, nao);
    }

    private void assegurarPautaComSecao(SessaoVotacao sessao) {
        if (sessao == null) {
            throw new IllegalStateException("Pauta ainda não possui sessão.");
        }
    }

    private void assegurarVotoValido(String cpfAssociado) {
        var naoPodeVotar = !validadorCPF.podeVotar(cpfAssociado);
        if (naoPodeVotar) {
            throw new IllegalStateException("Associado com CPF " + cpfAssociado + " não está apto a votar.");
        }
    }

    private void assegurarVotoUnicoPorSessao(String cpfAssociado, SessaoVotacao sessao) {
        var votoJaComputado = votoRepository.existsBySessaoIdAndCpfAssociado(sessao.getId(), cpfAssociado);
        if (votoJaComputado) {
            throw new IllegalStateException("Associado já votou nesta sessão.");
        }
    }

    private void assegurarSessaoAtiva(SessaoVotacao sessao) {
        if (sessao == null || !sessao.estaAberta()) {
            throw new IllegalStateException("Sessão de votação não está ativa.");
        }
    }

}

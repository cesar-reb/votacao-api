package com.cesar.votacao.api.application.service;

import com.cesar.votacao.api.domain.exception.SessaoNotFoundException;
import com.cesar.votacao.api.domain.model.Pauta;
import com.cesar.votacao.api.domain.model.ResultadoVotacao;
import com.cesar.votacao.api.domain.model.SessaoVotacao;
import com.cesar.votacao.api.domain.model.Voto;
import com.cesar.votacao.api.port.dto.ContagemVotoProjecao;
import com.cesar.votacao.api.port.out.PautaRepository;
import com.cesar.votacao.api.port.out.ValidadorCPF;
import com.cesar.votacao.api.port.out.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.cesar.votacao.api.domain.model.Voto.OpcaoVoto.SIM;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class VotacaoUseCaseImplTest {

    @Mock
    private PautaRepository pautaRepository;
    @Mock
    private VotoRepository votoRepository;
    @Mock
    private ValidadorCPF validadorCPF;

    @InjectMocks
    private VotacaoUseCaseImpl votacaoUseCase;

    private final Long pautaId = 1L;
    private final String cpf = "12345678900";

    @Nested
    @DisplayName("votar")
    class Votar {

        private Pauta pauta;
        private SessaoVotacao sessao;

        @BeforeEach
        void setup() {
            sessao = mock(SessaoVotacao.class);

            pauta = mock(Pauta.class);
        }

        @Test
        @DisplayName("deve registrar voto com sucesso")
        void deveVotarComSucesso() {
            given(pautaRepository.findById(pautaId)).willReturn(Optional.of(pauta));
            given(votoRepository.existsBySessaoIdAndCpfAssociado(any(), eq(cpf))).willReturn(false);
            given(validadorCPF.podeVotar(cpf)).willReturn(true);
            given(pauta.getSessao()).willReturn(sessao);
            given(sessao.estaAberta()).willReturn(true);

            assertThatCode(() -> votacaoUseCase.votar(cpf, pautaId, SIM))
                    .doesNotThrowAnyException();

            then(votoRepository).should().save(any(Voto.class));
        }

        @Test
        @DisplayName("deve lançar exceção se pauta não existir")
        void deveFalharSePautaNaoExiste() {
            given(pautaRepository.findById(pautaId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> votacaoUseCase.votar(cpf, pautaId, SIM))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Pauta não encontrada com ID: " + pautaId);
        }

        @Test
        @DisplayName("deve lançar exceção se sessão estiver inativa")
        void deveFalharSeSessaoInativa() {
            given(pautaRepository.findById(pautaId)).willReturn(Optional.of(pauta));
            given(pauta.getSessao()).willReturn(sessao);
            given(sessao.estaAberta()).willReturn(false);

            assertThatThrownBy(() -> votacaoUseCase.votar(cpf, pautaId, SIM))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Sessão de votação não está ativa.");
        }

        @Test
        @DisplayName("deve lançar exceção se já tiver votado")
        void deveFalharSeJaVotou() {
            given(pautaRepository.findById(pautaId)).willReturn(Optional.of(pauta));
            given(votoRepository.existsBySessaoIdAndCpfAssociado(any(), eq(cpf))).willReturn(true);
            given(pauta.getSessao()).willReturn(sessao);
            given(sessao.estaAberta()).willReturn(true);

            assertThatThrownBy(() -> votacaoUseCase.votar(cpf, pautaId, SIM))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Associado já votou nesta sessão.");
        }

        @Test
        @DisplayName("deve lançar exceção se CPF não puder votar")
        void deveFalharSeCpfInvalido() {
            given(pautaRepository.findById(pautaId)).willReturn(Optional.of(pauta));
            given(votoRepository.existsBySessaoIdAndCpfAssociado(any(), eq(cpf))).willReturn(false);
            given(validadorCPF.podeVotar(cpf)).willReturn(false);
            given(pauta.getSessao()).willReturn(sessao);
            given(sessao.estaAberta()).willReturn(true);

            assertThatThrownBy(() -> votacaoUseCase.votar(cpf, pautaId, SIM))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Associado com CPF " + cpf + " não está apto a votar.");
        }
    }

    @Nested
    @DisplayName("obterResultado")
    class ObterResultado {

        private Pauta pauta;
        private SessaoVotacao sessao;

        @BeforeEach
        void setup() {
            sessao = mock(SessaoVotacao.class);
            pauta = mock(Pauta.class);
        }

        @Test
        @DisplayName("deve retornar contagem de votos corretamente")
        void deveRetornarContagemDeVotos() {
            given(pautaRepository.findById(pautaId)).willReturn(Optional.of(pauta));
            given(sessao.getId()).willReturn(10L);
            given(pauta.getSessao()).willReturn(sessao);

            var sim = getSim();
            var nao = getNao();

            given(votoRepository.contarVotosPorSessao(10L)).willReturn(List.of(sim, nao));

            ResultadoVotacao resultado = votacaoUseCase.obterResultado(pautaId);

            assertThat(resultado.totalSim()).isEqualTo(3);
            assertThat(resultado.totalNao()).isEqualTo(2);
        }

        @Test
        @DisplayName("deve lançar exceção se pauta não tiver sessão")
        void deveFalharSeNaoTiverSessao() {
            given(pauta.getSessao()).willReturn(null);
            given(pautaRepository.findById(pautaId)).willReturn(Optional.of(pauta));


            assertThatThrownBy(() -> votacaoUseCase.obterResultado(pautaId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Pauta ainda não possui sessão.");
        }

        @Test
        @DisplayName("deve lançar exceção se pauta não for encontrada")
        void deveFalharSePautaNaoExiste() {
            given(pautaRepository.findById(pautaId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> votacaoUseCase.obterResultado(pautaId))
                    .isInstanceOf(SessaoNotFoundException.class)
                    .hasMessage("Pauta não encontrada com ID: " + pautaId);
        }


        private ContagemVotoProjecao getNao() {
            return new ContagemVotoProjecao() {
                @Override
                public String getOpcao() {
                    return "NAO";
                }

                @Override
                public Integer getTotal() {
                    return 2;
                }
            };
        }

        private ContagemVotoProjecao getSim() {
            return new ContagemVotoProjecao() {
                @Override
                public String getOpcao() {
                    return "SIM";
                }

                @Override
                public Integer getTotal() {
                    return 3;
                }
            };
        }
    }

}
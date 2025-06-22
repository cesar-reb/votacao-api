package com.cesar.votacao.api.application.service;

import com.cesar.votacao.api.domain.model.Pauta;
import com.cesar.votacao.api.port.out.PautaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PautaServiceImplTest {

    @Mock
    private PautaRepository pautaRepository;
    @InjectMocks
    private PautaServiceImpl pautaService;

    @Nested
    @DisplayName("criarPauta")
    class CriarPauta {
        private String titulo;
        private String descricao;
        private UUID usuarioCriadorId;

        private Pauta pautaEsperada;
        private Pauta pautaResultado;

        @Test
        @DisplayName("deve criar pauta com título, descrição e ID do criador")
        void deveCriarPautaComSucesso() {

            dadoParametrosValidosParaCriarPauta();
            dadoRepositorioSalvaPautaCorretamente();

            quandoPautaServiceCriaPautaCorretamente();

            entaoEsperaRepositorioChamdoSalvarUmaVez();
            entaoEsperaResultadoValido();

        }

        private void entaoEsperaResultadoValido() {
            assertEquals(pautaEsperada.getTitulo(), pautaResultado.getTitulo());
            assertEquals(pautaEsperada.getDescricao(), pautaResultado.getDescricao());
            assertEquals(pautaEsperada.getUsuarioCriadorId(), pautaResultado.getUsuarioCriadorId());
        }

        private void dadoRepositorioSalvaPautaCorretamente() {
            pautaEsperada = new Pauta(titulo, descricao, usuarioCriadorId);
            doReturn(pautaEsperada).when(pautaRepository).save(any(Pauta.class));
        }

        void dadoParametrosValidosParaCriarPauta() {
            titulo = "Título da Pauta";
            descricao = "Descrição da Pauta";
            usuarioCriadorId = UUID.randomUUID();
        }

        private void quandoPautaServiceCriaPautaCorretamente() {
            pautaResultado = pautaService.criarPauta(titulo, descricao, usuarioCriadorId);
        }


    }

    @Nested
    @DisplayName("abrirSessao")
    class AbrirSessao {

        Long pautaId = 1L;
        Long duracao = 5L;
        Pauta pautaMock;

        @BeforeEach
        void init() {
            pautaMock = mock(Pauta.class);
        }

        @Test
        @DisplayName("deve abrir sessão com duração especificada")
        void deveAbrirSessaoComDuracaoInformada() {

            dadoPautaSolicitadaEncontrada();

            quandoAbreSessaoCorretamente(pautaId, duracao);

            entaoRepositoryFindByIdExecutadoUmaVez();
            entaoEsperaRepositorioChamdoSalvarUmaVez();
            entaoEsperaSessaoAberta(duracao);
        }

        @Test
        @DisplayName("deve abrir sessão com duração padrão se não informada")
        void deveAbrirSessaoComDuracaoPadrao() {
            dadoPautaSolicitadaEncontrada();

            quandoAbreSessaoCorretamente(pautaId, null);

            entaoRepositoryFindByIdExecutadoUmaVez();
            entaoEsperaRepositorioChamdoSalvarUmaVez();
            entaoEsperaSessaoAberta(1L);
        }


        @Test
        @DisplayName("deve lançar exceção se pauta não for encontrada")
        void deveLancarExcecaoSePautaNaoEncontrada() {

            dadoPautaSolicitadaNaoEncontrada();

            quandoAbreSessaoNaoEncontraPautaSolicitada();

            entaoRepositoryFindByIdExecutadoUmaVez();
            entaoEsperaRepositorioNaoChamadoSalvar();

        }

        private void dadoPautaSolicitadaNaoEncontrada() {
            doReturn(Optional.empty()).when(pautaRepository).findById(anyLong());

        }

        private void dadoPautaSolicitadaEncontrada() {
            doReturn(Optional.of(pautaMock)).when(pautaRepository).findById(anyLong());
        }

        private void quandoAbreSessaoNaoEncontraPautaSolicitada() {
            assertThatThrownBy(() -> pautaService.abrirSessao(pautaId, duracao))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Pauta não encontrada com ID: " + pautaId);
        }

        private void quandoAbreSessaoCorretamente(Long pautaId, Long duracaoEmMinutos) {
            assertDoesNotThrow(() -> pautaService.abrirSessao(pautaId, duracaoEmMinutos));
        }

        private void entaoEsperaRepositorioNaoChamadoSalvar() {
            verify(pautaRepository, times(0)).save(any(Pauta.class));
        }

        private void entaoEsperaSessaoAberta(Long duracao) {
            then(pautaMock).should().abrirSessao(duracao);
        }

        private void entaoRepositoryFindByIdExecutadoUmaVez() {
            verify(pautaRepository, times(1)).findById(anyLong());
        }
    }

    private void entaoEsperaRepositorioChamdoSalvarUmaVez() {
        verify(pautaRepository, times(1)).save(any(Pauta.class));
    }
}
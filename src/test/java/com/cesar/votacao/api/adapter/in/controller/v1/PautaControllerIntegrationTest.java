package com.cesar.votacao.api.adapter.in.controller.v1;

import com.cesar.votacao.api.adapter.in.controller.dto.request.AbrirSessaoRequest;
import com.cesar.votacao.api.adapter.in.controller.dto.request.NovaPautaRequest;
import com.cesar.votacao.api.adapter.in.controller.dto.request.VotoRequest;
import com.cesar.votacao.api.domain.model.Voto;
import com.cesar.votacao.api.port.out.ValidadorCPF;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PautaControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private ValidadorCPF validadorCPF;

    @Nested
    @DisplayName("POST /v1/pautas")
    class CriarPauta {

        @Test
        @DisplayName("deve criar uma nova pauta com sucesso")
        void deveCriarNovaPauta() throws Exception {
            var request = new NovaPautaRequest("Pauta Teste", "Descrição");
            var userId = UUID.randomUUID();

            mockMvc.perform(post("/v1/pautas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("userId", userId)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.titulo").value("Pauta Teste"))
                    .andExpect(jsonPath("$.descricao").value("Descrição"));
        }
    }

    @Nested
    @DisplayName("POST /v1/pautas/{id}/sessao")
    class AbrirSessao {

        @Test
        @DisplayName("deve abrir sessão para pauta existente")
        void deveAbrirSessao() throws Exception {

            // cria pauta
            var userId = UUID.randomUUID();
            var novaPauta = new NovaPautaRequest("Pauta Sessão", "descrição");
            var result = mockMvc.perform(post("/v1/pautas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("userId", userId)
                            .content(objectMapper.writeValueAsString(novaPauta)))
                    .andReturn();

            var pautaId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

            // abre sessão
            var request = new AbrirSessaoRequest(3L);

            mockMvc.perform(post("/v1/pautas/" + pautaId + "/sessao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST /v1/pautas/{id}/voto")
    class Votar {

        @Test
        @DisplayName("deve votar com sucesso em pauta com sessão aberta")
        void deveVotar() throws Exception {
            //cria pauta
            var userId = UUID.randomUUID();
            var novaPauta = new NovaPautaRequest("Pauta Votação", "descrição");
            var pautaResponse = mockMvc.perform(post("/v1/pautas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("userId", userId)
                            .content(objectMapper.writeValueAsString(novaPauta)))
                    .andReturn();

            // abre sessão
            var pautaId = objectMapper.readTree(pautaResponse.getResponse().getContentAsString()).get("id").asLong();

            mockMvc.perform(post("/v1/pautas/" + pautaId + "/sessao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new AbrirSessaoRequest(5L))))
                    .andExpect(status().isOk());

            // computa voto
            var voto = new VotoRequest("12345678901", Voto.OpcaoVoto.SIM);
            given(validadorCPF.podeVotar("12345678901")).willReturn(true);

            mockMvc.perform(post("/v1/pautas/" + pautaId + "/voto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(voto)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /v1/pautas/{id}/resultado")
    class Resultado {

        @Test
        @DisplayName("deve retornar resultado com sucesso")
        void deveRetornarResultado() throws Exception {
            // cria pauta
            var userId = UUID.randomUUID();
            var novaPauta = new NovaPautaRequest("Pauta Resultado", "descrição");
            var result = mockMvc.perform(post("/v1/pautas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("userId", userId)
                            .content(objectMapper.writeValueAsString(novaPauta)))
                    .andReturn();

            // abre sessão
            var pautaId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

            mockMvc.perform(post("/v1/pautas/" + pautaId + "/sessao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new AbrirSessaoRequest(5L))))
                    .andExpect(status().isOk());

            // computa voto
            var voto = new VotoRequest("12345678901", Voto.OpcaoVoto.SIM);
            given(validadorCPF.podeVotar("12345678901")).willReturn(true);

            mockMvc.perform(post("/v1/pautas/" + pautaId + "/voto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(voto)))
                    .andExpect(status().isOk());

            // consulta votos
            mockMvc.perform(get("/v1/pautas/" + pautaId + "/resultado"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalSim").value(1))
                    .andExpect(jsonPath("$.totalNao").value(0));
        }
    }

    @TestConfiguration
    static class Mocks {
        @Bean
        public ValidadorCPF validadorCPF() {
            return mock(ValidadorCPF.class);
        }
    }
}
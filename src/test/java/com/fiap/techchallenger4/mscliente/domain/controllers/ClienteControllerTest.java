package com.fiap.techchallenger4.mscliente.domain.controllers;

import br.com.fiap.estrutura.exception.BusinessException;
import br.com.fiap.estrutura.exception.EntidadeNaoEncontrada;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoRequest;
import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoResponse;
import com.fiap.techchallenger4.mscliente.domain.services.ClienteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@ExtendWith(SpringExtension.class)
public class ClienteControllerTest {

    private AutoCloseable autoCloseable;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @InjectMocks
    ClienteController clienteController;

    @BeforeEach
    void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Nested
    class CadastrarCliente {

        private static String asJsonString(final Object obj) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        void deveRetornarBadRequestQuandoCadastrarClienteComDadosInvalidos() throws Exception {
            // Dado (Given)
            ClienteDtoRequest clienteDtoInvalido = new ClienteDtoRequest("", "", "", "", "", "", "", "", "", "", "");
            given(clienteService.cadastrarCliente(any(ClienteDtoRequest.class))).willThrow(BusinessException.class);

            // Quando (When)
            mockMvc.perform(post("/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(clienteDtoInvalido)))
                    // Então (Then)
                    .andExpect(status().isBadRequest());
        }

        @Test
        void deveRetornarStatusCreatedQuandoCadastrarClienteComSucesso() throws Exception {
            // Dado (Given)
            ClienteDtoRequest clienteDtoRequest = new ClienteDtoRequest("Maria Silva", "987.654.321-09", "maria@example.com", "87654-321", "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321");
            ClienteDtoResponse clienteDtoResponse = new ClienteDtoResponse(null, "Maria Silva", "987.654.321-09", "maria@example.com", "87654-321", "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321", null);

            given(clienteService.cadastrarCliente(any(ClienteDtoRequest.class))).willReturn(clienteDtoResponse);

            // Quando (When)
            mockMvc.perform(post("/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(clienteDtoRequest)))
                    // Então (Then)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.nome").value("Maria Silva"));
        }
    }

    @Nested
    class BuscarCliente {
        @Test
        void deveListarClientesComSucesso() throws Exception {
            // Dado (Given)
            ClienteDtoResponse cliente1 = new ClienteDtoResponse(1L, "Maria Silva", "987.654.321-09", "maria@example.com", "87654-321", "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321", LocalDateTime.now());
            ClienteDtoResponse cliente2 = new ClienteDtoResponse(2L, "João Costa", "123.456.789-00", "joao@example.com", "12345-678", "Rua Velha", "200", "Apto 1", "Bairro Antigo", "Cidade Velha", "SP", "(11) 96432-1234", LocalDateTime.now());
            List<ClienteDtoResponse> clientes = Arrays.asList(cliente1, cliente2);
            given(clienteService.listarClientes()).willReturn(clientes);

            // Quando (When)
            mockMvc.perform(get("/clientes")
                            .contentType(MediaType.APPLICATION_JSON))
                    // Então (Then)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nome").value("Maria Silva"))
                    .andExpect(jsonPath("$[1].nome").value("João Costa"));
        }

        @Test
        void deveRetornarStatusNotFoundQuandoClienteNaoEncontrado() throws Exception {
            Long codigoCliente = 2L;
            given(clienteService.buscarClientePorCodigo(codigoCliente)).willThrow(new EntidadeNaoEncontrada("Cliente com código " + codigoCliente + " não encontrado"));

            mockMvc.perform(get("/clientes/{codigoCliente}", codigoCliente)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deveRetornarClienteQuandoEncontrado() throws Exception {
            Long codigoCliente = 1L;
            ClienteDtoResponse clienteResponse = new ClienteDtoResponse(codigoCliente, "Maria Silva", "987.654.321-09", "maria@example.com", "87654-321", "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321", LocalDateTime.now());
            given(clienteService.buscarClientePorCodigo(codigoCliente)).willReturn(clienteResponse);

            mockMvc.perform(get("/clientes/{codigoCliente}", codigoCliente)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Maria Silva"));
        }

        @Test
        void deveRetornarStatusNotFoundQuandoEmailNaoEncontrado() throws Exception {
            String email = "inexistente@example.com";
            given(clienteService.buscarClientePorEmail(email)).willThrow(new EntidadeNaoEncontrada("Cliente com Email " + email + " não encontrado"));

            mockMvc.perform(get("/clientes/email/{email}", email)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deveRetornarClienteQuandoEmailEncontrado() throws Exception {
            String email = "maria@example.com";
            ClienteDtoResponse clienteResponse = new ClienteDtoResponse(1L, "Maria Silva", "987.654.321-09", email, "87654-321", "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321", LocalDateTime.now());
            given(clienteService.buscarClientePorEmail(email)).willReturn(clienteResponse);

            mockMvc.perform(get("/clientes/email/{email}", email)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(email));
        }
    }

    @Nested
    class AtualizarCliente {
        @Test
        void deveRetornarStatusNotFoundQuandoClienteNaoEncontrado() throws Exception {
            Long codigoCliente = 99L;
            ClienteDtoRequest clienteDto = new ClienteDtoRequest("Teste", "123.456.789-01", "teste@notfound.com", "12345-678", "Rua Inexistente", "500", "Apto 5", "Bairro Fantasma", "Cidade Fictícia", "XX", "(00) 00000-0000");

            given(clienteService.atualizarClientePorCodigo(eq(codigoCliente), any(ClienteDtoRequest.class)))
                    .willThrow(new EntidadeNaoEncontrada("Cliente com código " + codigoCliente + " não encontrado"));

            mockMvc.perform(put("/clientes/{codigoCliente}", codigoCliente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(clienteDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deveRetornarStatusOkAoAtualizarCliente() throws Exception {
            Long codigoCliente = 1L;
            ClienteDtoRequest clienteDto = new ClienteDtoRequest("Maria Silva", "987.654.321-09", "maria@update.com", "87654-321", "Rua Atualizada", "101", "Apto 202", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321");
            ClienteDtoResponse expectedResponse = new ClienteDtoResponse(codigoCliente, clienteDto.nome(), clienteDto.cpf(), clienteDto.email(), clienteDto.cep(), clienteDto.logradouro(), clienteDto.numero(), clienteDto.complemento(), clienteDto.bairro(), clienteDto.cidade(), clienteDto.estado(), clienteDto.telefone(), LocalDateTime.now());

            given(clienteService.atualizarClientePorCodigo(eq(codigoCliente), any(ClienteDtoRequest.class))).willReturn(expectedResponse);

            mockMvc.perform(put("/clientes/{codigoCliente}", codigoCliente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(clienteDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Maria Silva"));
        }

        @Test
        void deveRetornarStatusNotFoundQuandoClientePorEmailNaoEncontrado() throws Exception {
            String email = "notfound@example.com";
            ClienteDtoRequest requestDto = new ClienteDtoRequest("Maria Silva", "987.654.321-09", "maria@notfound.com", "87654-321", "Rua Inexistente", "500", "Apto 5", "Bairro Fantasma", "Cidade Fictícia", "XX", "(00) 00000-0000");

            // Usando uma exceção mais específica para clarificar o resultado esperado
            given(clienteService.atualizarClientePorEmail(eq(email), any(ClienteDtoRequest.class)))
                    .willThrow(new EntidadeNaoEncontrada("Cliente com Email " + email + " não encontrado"));

            mockMvc.perform(put("/clientes/email/{email}", email)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deveRetornarStatusOkAoAtualizarClientePorEmail() throws Exception {
            String email = "test@example.com";
            ClienteDtoRequest requestDto = new ClienteDtoRequest("Maria Silva", "987.654.321-09", "maria@update.com", "87654-321", "Rua Atualizada", "101", "Apto 202", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321");
            ClienteDtoResponse responseDto = new ClienteDtoResponse(1L, "Maria Silva", "987.654.321-09", "maria@update.com", "87654-321", "Rua Atualizada", "101", "Apto 202", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321", LocalDateTime.now());

            given(clienteService.atualizarClientePorEmail(eq(email), any(ClienteDtoRequest.class))).willReturn(responseDto);

            mockMvc.perform(put("/clientes/email/{email}", email)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Maria Silva"));
        }
    }

    @Nested
    class ExcluirCliente {

        @Test
        void deveRetornarNoContentQuandoClienteExcluidoComSucesso() throws Exception {
            Long codigoCliente = 1L;
            doNothing().when(clienteService).excluirClientePorCodigo(codigoCliente);

            mockMvc.perform(delete("/clientes/{codigoCliente}", codigoCliente))
                    .andExpect(status().isNoContent());

            verify(clienteService, times(1)).excluirClientePorCodigo(codigoCliente);
        }

        @Test
        void deveRetornarNotFoundQuandoClienteNaoEncontrado() throws Exception {
            Long codigoCliente = 99L; // Assumindo que 99 é um ID inexistente
            // Usar EntidadeNaoEncontrada ou outra exceção personalizada que seu sistema trata como 404
            doThrow(new EntidadeNaoEncontrada("Cliente não encontrado")).when(clienteService).excluirClientePorCodigo(codigoCliente);

            mockMvc.perform(delete("/clientes/{codigoCliente}", codigoCliente))
                    .andExpect(status().isNotFound());

            verify(clienteService, times(1)).excluirClientePorCodigo(codigoCliente);
        }

        @Test
        void deveRetornarNoContentQuandoClienteExcluidoComSucessoPorEmail() throws Exception {
            String email = "cliente@example.com";

            // Simular que a exclusão será bem-sucedida
            doNothing().when(clienteService).excluirClientePorEmail(email);

            // Executar e verificar
            mockMvc.perform(delete("/clientes/email/{email}", email))
                    .andExpect(status().isNoContent());

            verify(clienteService).excluirClientePorEmail(email);
        }

        @Test
        void deveRetornarNotFoundQuandoClienteNaoEncontradoPorEmail() throws Exception {
            String email = "inexistente@example.com";

            // Simular que o cliente não é encontrado
            doThrow(new EntidadeNaoEncontrada("Cliente com Email " + email + " não encontrado"))
                    .when(clienteService).excluirClientePorEmail(email);

            // Executar e verificar
            mockMvc.perform(delete("/clientes/email/{email}", email))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Cliente com Email " + email + " não encontrado"));

            verify(clienteService).excluirClientePorEmail(email);
        }
    }
}

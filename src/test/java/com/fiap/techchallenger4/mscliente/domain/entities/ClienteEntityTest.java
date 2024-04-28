package com.fiap.techchallenger4.mscliente.domain.entities;

import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoRequest;
import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteEntityTest {

    ClienteEntity cliente;

    @BeforeEach
    void setUp() {
        cliente = new ClienteEntity();
    }

    @Test
    void deveAtribuirValoresCorretamente() {
        ClienteEntity cliente = new ClienteEntity(1L, "Ana Silva", "123.456.789-09", "ana@example.com", "12345-000",
                "Rua das Flores", "100", "Apto 10", "Jardim", "São Paulo", "SP", "(11) 99876-5432");
        assertNotNull(cliente);
        assertEquals(1L, cliente.getCodigoCliente());
        assertEquals("Ana Silva", cliente.getNome());
        assertEquals("123.456.789-09", cliente.getCpf());
        assertEquals("ana@example.com", cliente.getEmail());
        assertEquals("12345-000", cliente.getCep());
        assertEquals("Rua das Flores", cliente.getLogradouro());
        assertEquals("100", cliente.getNumero());
        assertEquals("Apto 10", cliente.getComplemento());
        assertEquals("Jardim", cliente.getBairro());
        assertEquals("São Paulo", cliente.getCidade());
        assertEquals("SP", cliente.getEstado());
        assertEquals("(11) 99876-5432", cliente.getTelefone());
    }

    @Test
    void deveRetornarDtoCorretamente() {
        ClienteEntity cliente = new ClienteEntity(1L, "João Silva", "123.456.789-00", "joao@example.com", "12345-678",
                "Rua A", "100", "Apto 1", "Bairro B", "Cidade C", "SP", "(11) 91234-5678");
        ClienteDtoResponse dto = cliente.toDto();
        assertNotNull(dto);
        assertEquals(cliente.getCodigoCliente(), dto.codigoCliente());
        assertEquals(cliente.getNome(), dto.nome());
        assertEquals(cliente.getCpf(), dto.cpf());
        assertEquals(cliente.getEmail(), dto.email());
        assertEquals(cliente.getCep(), dto.cep());
        assertEquals(cliente.getLogradouro(), dto.logradouro());
        assertEquals(cliente.getNumero(), dto.numero());
        assertEquals(cliente.getComplemento(), dto.complemento());
        assertEquals(cliente.getBairro(), dto.bairro());
        assertEquals(cliente.getCidade(), dto.cidade());
        assertEquals(cliente.getEstado(), dto.estado());
        assertEquals(cliente.getTelefone(), dto.telefone());
        assertEquals(cliente.getDataCriacao(), dto.dataCriacao());
    }

    @Test
    void dtoRequestDeveConverterParaEntidadeCorretamente() {
        ClienteDtoRequest dto = new ClienteDtoRequest(
                "Maria Silva", // nome
                "123.456.789-01", // cpf
                "maria@example.com", // email
                "12345-678", // cep
                "Rua Sol", // logradouro
                "100", // número
                "", // complemento
                "Centro", // bairro
                "São Paulo", // cidade
                "SP", // estado
                "(11) 98765-4321" // telefone
        );

        ClienteEntity entity = dto.toEntity();

        assertNull(entity.getCodigoCliente()); // ID deve ser nulo porque é gerado pelo banco de dados
        assertEquals("Maria Silva", entity.getNome());
        assertEquals("123.456.789-01", entity.getCpf());
        assertEquals("maria@example.com", entity.getEmail());
        assertEquals("12345-678", entity.getCep());
        assertEquals("Rua Sol", entity.getLogradouro());
        assertEquals("100", entity.getNumero());
        assertEquals("", entity.getComplemento()); // Verifica se o complemento, mesmo vazio, é tratado corretamente
        assertEquals("Centro", entity.getBairro());
        assertEquals("São Paulo", entity.getCidade());
        assertEquals("SP", entity.getEstado());
        assertEquals("(11) 98765-4321", entity.getTelefone());
    }

    @Test
    void deveSetarNomeCorretamente() {
        String nomeEsperado = "João Silva";
        cliente.setNome(nomeEsperado);
        assertEquals(nomeEsperado, cliente.getNome());
    }

    @Test
    void deveSetarEmailCorretamente() {
        String emailEsperado = "joao@example.com";
        cliente.setEmail(emailEsperado);
        assertEquals(emailEsperado, cliente.getEmail());
    }

    @Test
    void deveSetarTelefoneCorretamente() {
        String telefoneEsperado = "(11) 91234-5678";
        cliente.setTelefone(telefoneEsperado);
        assertEquals(telefoneEsperado, cliente.getTelefone());
    }

    @Test
    void deveCriarClienteEntitySemValores() {
        ClienteEntity cliente = new ClienteEntity();
        assertNotNull(cliente);
    }
}

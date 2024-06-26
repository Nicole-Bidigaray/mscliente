package com.fiap.techchallenger4.mscliente.domain.services;

import br.com.fiap.estrutura.exception.BusinessException;

import com.fiap.techchallenger4.mscliente.domain.consumer.PedidoConsumerFeignClient;
import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoRequest;
import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoResponse;
import com.fiap.techchallenger4.mscliente.domain.entities.ClienteEntity;
import com.fiap.techchallenger4.mscliente.domain.repositories.ClienteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private PedidoConsumerFeignClient consumerFeignClient;
    @InjectMocks
    ClienteService clienteService;

    ClienteEntity clienteExistente;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        clienteExistente = new ClienteEntity(1L, "João Silva", "123.456.789-00", "joao@example.com", "12345-678", "Rua A", "100", "Apto 1", "Bairro B", "Cidade C", "SP", "(11) 91234-5678");
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        // Configuração
        Long codigoCliente = 1L;
        when(clienteRepository.findByCodigoCliente(codigoCliente)).thenReturn(null);

        assertThrows(BusinessException.class, () -> clienteService.buscarClientePorCodigo(codigoCliente));
        verify(clienteRepository).findByCodigoCliente(codigoCliente);
    }

    @Test
    void deveEncontrarClientePorCodigo() throws BusinessException {
        // Configuração
        Long codigoCliente = 1L;
        ClienteEntity clienteMock = new ClienteEntity();
        clienteMock.setCodigoCliente(codigoCliente);
        when(clienteRepository.findByCodigoCliente(codigoCliente)).thenReturn(clienteMock);

        // Execução
        ClienteDtoResponse result = clienteService.buscarClientePorCodigo(codigoCliente);

        // Verificação
        assertNotNull(result);
        assertEquals(codigoCliente, result.codigoCliente());
        verify(clienteRepository).findByCodigoCliente(codigoCliente);
    }

    @Test
    void deveLancarExcecaoQuandoClientePorEmailNaoEncontrado() {
        // Configuração
        String email = "inexistente@example.com";
        when(clienteRepository.findByEmail(email)).thenReturn(null);

        // Ação e Verificação
        BusinessException exception = assertThrows(BusinessException.class, () -> clienteService.buscarClientePorEmail(email));
        assertEquals("Cliente com Email " + email + " não encontrado", exception.getMessage());
        verify(clienteRepository).findByEmail(email);
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastradoEmOutroCliente() {
        // Cliente existente com e-mail original
        ClienteEntity clienteExistente = new ClienteEntity(1L, "Maria Silva", "987.654.321-09", "maria@original.com", "12345-678",
                "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321");

        // Tentativa de atualizar para um e-mail que já pertence a outro cliente
        ClienteDtoRequest clienteAtualizacao = new ClienteDtoRequest(
                "Maria Silva", "987.654.321-09", "email@existente.com", "12345-678",
                "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321");

        // Configurar o repositório para simular que o e-mail já existe no sistema
        when(clienteRepository.existsByEmail("email@existente.com")).thenReturn(true);

        // Ação e Verificação
        BusinessException exception = assertThrows(BusinessException.class,
                () -> clienteService.atualizarCliente(clienteAtualizacao, clienteExistente));

        assertEquals("Email já cadastrado.", exception.getMessage());
        verify(clienteRepository).existsByEmail("email@existente.com");
    }

    @Test
    void deveLancarExcecaoAoTentarAtualizarParaEmailJaUsadoPorOutroCliente() {
        // Cliente original que será atualizado
        ClienteEntity clienteExistente = new ClienteEntity(
                1L, "João Silva", "123.456.789-00", "joao@original.com", "12345-678",
                "Rua A", "100", "Apto 1", "Bairro B", "Cidade C", "SP", "(11) 91234-5678");

        // DTO com os novos dados, incluindo um e-mail que já está em uso por outro cliente
        ClienteDtoRequest atualizacaoDto = new ClienteDtoRequest(
                "João Silva Atualizado", "123.456.789-00", "email@usado.com", "12345-678",
                "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "SP", "(11) 99876-5432");

        // Configurando o repositório para indicar que o e-mail já está em uso
        when(clienteRepository.findByEmail("joao@original.com")).thenReturn(clienteExistente);
        when(clienteRepository.existsByEmail("email@usado.com")).thenReturn(true);

        // Ação e verificação
        BusinessException exception = assertThrows(BusinessException.class,
                () -> clienteService.atualizarCliente(atualizacaoDto, clienteExistente));

        assertEquals("Email já cadastrado.", exception.getMessage());
        verify(clienteRepository).existsByEmail("email@usado.com");
    }

    @Test
    void deveEncontrarClientePorEmail() throws BusinessException {
        // Configuração
        String email = "cliente@example.com";
        ClienteEntity clienteMock = new ClienteEntity();
        clienteMock.setEmail(email);
        when(clienteRepository.findByEmail(email)).thenReturn(clienteMock);

        // Ação
        ClienteDtoResponse result = clienteService.buscarClientePorEmail(email);

        // Verificação
        assertNotNull(result);
        assertEquals(email, result.email());
        verify(clienteRepository).findByEmail(email);
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        // Cliente já existe com o CPF
        ClienteDtoRequest novoCliente = new ClienteDtoRequest("Maria Silva", "123.456.789-01", "maria@example.com", "12345-678", "Rua Sol", "100", "", "Centro", "São Paulo", "SP", "(11) 98765-4321");
        when(clienteRepository.existsByCpf(novoCliente.cpf())).thenReturn(true);

        // Ação e Verificação
        BusinessException exception = assertThrows(BusinessException.class, () -> clienteService.cadastrarCliente(novoCliente));
        assertEquals("CPF já cadastrado.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoCpfInvalidoOuVazio() {
        // Testando com CPF vazio
        ClienteDtoRequest clienteComCpfInvalido = new ClienteDtoRequest(
                "Maria Silva", "", "maria@example.com", "12345-678",
                "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> clienteService.cadastrarCliente(clienteComCpfInvalido));

        assertEquals("CPF inválido ou vazio. Deve estar no formato XXX.XXX.XXX-XX.", exception.getMessage());

        // Testando com CPF no formato errado
        ClienteDtoRequest clienteComCpfFormatoErrado = new ClienteDtoRequest(
                "Maria Silva", "98765432109", "maria@example.com", "12345-678",
                "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321");

        exception = assertThrows(BusinessException.class,
                () -> clienteService.cadastrarCliente(clienteComCpfFormatoErrado));

        assertEquals("CPF inválido ou vazio. Deve estar no formato XXX.XXX.XXX-XX.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaCadastradoEmOutroClienteNaAtualizacao() {
        // Dados de entrada
        Long clienteId = 1L;
        ClienteDtoRequest atualizacaoDto = new ClienteDtoRequest("João Silva", "456.789.123-99", "joao.silva@novoemail.com", "12345-678", "Rua das Flores", "100", "Apto 101", "Centro", "São Paulo", "SP", "(11) 91234-5678");

        ClienteEntity clienteExistente = new ClienteEntity(clienteId, "João Silva", "123.456.789-00", "joao@original.com", "12345-678", "Rua A", "100", "Apto 1", "Bairro B", "Cidade C", "SP", "(11) 91234-5678");

        // Configurando o repository para retornar um cliente diferente para o mesmo CPF
        when(clienteRepository.findByCodigoCliente(clienteId)).thenReturn(clienteExistente);
        when(clienteRepository.existsByCpf(atualizacaoDto.cpf())).thenReturn(true);

        // Ação e Verificação
        BusinessException exception = assertThrows(BusinessException.class, () -> clienteService.atualizarCliente(atualizacaoDto, clienteExistente));
        assertEquals("CPF já cadastrado.", exception.getMessage());
        verify(clienteRepository, never()).save(clienteExistente);
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        // Cliente já existe com o Email
        ClienteDtoRequest novoCliente = new ClienteDtoRequest("Maria Silva", "123.456.789-01", "maria@already.com", "12345-678", "Rua Sol", "100", "", "Centro", "São Paulo", "SP", "(11) 98765-4321");
        when(clienteRepository.existsByEmail(novoCliente.email())).thenReturn(true);

        // Ação e Verificação
        BusinessException exception = assertThrows(BusinessException.class, () -> clienteService.cadastrarCliente(novoCliente));
        assertEquals("Email já cadastrado.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoEmailInvalidoOuVazio() {
        // Testando com e-mail vazio
        ClienteDtoRequest clienteComEmailInvalido = new ClienteDtoRequest(
                "Maria Silva", "987.654.321-09", "", "12345-678",
                "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> clienteService.cadastrarCliente(clienteComEmailInvalido));

        assertEquals("Email inválido ou vazio. Formato esperado: exemplo@dominio.com", exception.getMessage());

        // Testando com e-mail no formato errado
        ClienteDtoRequest clienteComEmailFormatoErrado = new ClienteDtoRequest(
                "Maria Silva", "987.654.321-09", "mariaexample.com", "12345-678",
                "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321");

        exception = assertThrows(BusinessException.class,
                () -> clienteService.cadastrarCliente(clienteComEmailFormatoErrado));

        assertEquals("Email inválido ou vazio. Formato esperado: exemplo@dominio.com", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastradoEmOutroClienteNaAtualizacao() {
        // Dados de entrada
        Long clienteId = 1L;
        ClienteDtoRequest atualizacaoDto = new ClienteDtoRequest(
                "João Silva", "123.456.789-00", "email@usadoporoutro.com",
                "12345-678", "Rua das Flores", "100", "Apto 101", "Centro",
                "São Paulo", "SP", "(11) 91234-5678");

        ClienteEntity clienteExistente = new ClienteEntity(
                clienteId, "João Silva", "123.456.789-00", "joao@original.com",
                "12345-678", "Rua A", "100", "Apto 1", "Bairro B", "Cidade C",
                "SP", "(11) 91234-5678");

        // Cliente existente no banco com o email que será testado para duplicidade
        when(clienteRepository.findByCodigoCliente(clienteId)).thenReturn(clienteExistente);
        when(clienteRepository.existsByEmail(atualizacaoDto.email())).thenReturn(true);

        // Ação e Verificação
        BusinessException exception = assertThrows(BusinessException.class,
                () -> clienteService.atualizarCliente(atualizacaoDto, clienteExistente));

        assertEquals("Email já cadastrado.", exception.getMessage());
        verify(clienteRepository).existsByEmail(atualizacaoDto.email());
        verify(clienteRepository, never()).save(clienteExistente);
    }

    @Test
    void naoDeveCriarClienteSemNomeInformado() {
        ClienteDtoRequest dto = new ClienteDtoRequest("", "123.456.789-01", "maria@example.com", "12345-678", "Rua Sol", "100", "", "Centro", "São Paulo", "SP", "(11) 98765-4321");

        BusinessException exception = assertThrows(BusinessException.class, () -> clienteService.cadastrarCliente(dto));
        assertEquals("Nome não pode ser vazio.", exception.getMessage());
    }

    @Test
    void naoDeveCriarClienteSemCpfInformado() {
        ClienteDtoRequest dto = new ClienteDtoRequest("Maria Silva", "", "maria@example.com", "12345-678", "Rua Sol", "100", "", "Centro", "São Paulo", "SP", "(11) 98765-4321");

        BusinessException exception = assertThrows(BusinessException.class, () -> clienteService.cadastrarCliente(dto));
        assertEquals("CPF inválido ou vazio. Deve estar no formato XXX.XXX.XXX-XX.", exception.getMessage());
    }

    @Test
    void naoDeveCriarClienteSemEmailInformado() {
        ClienteDtoRequest dto = new ClienteDtoRequest("Maria Silva", "123.456.789-01", "", "12345-678", "Rua Sol", "100", "", "Centro", "São Paulo", "SP", "(11) 98765-4321");

        assertThrows(BusinessException.class, () -> clienteService.cadastrarCliente(dto), "Email inválido ou vazio. Formato esperado: exemplo@dominio.com");
    }

    @Test
    void naoDeveCriarClienteSemCepInformado() {
        ClienteDtoRequest dto = new ClienteDtoRequest("Maria Silva", "123.456.789-01", "maria@example.com", "", "Rua Sol", "100", "", "Centro", "São Paulo", "SP", "(11) 98765-4321");

        assertThrows(BusinessException.class, () -> clienteService.cadastrarCliente(dto), "CEP inválido ou vazio. Deve estar no formato XXXXX-XXX.");
    }

    @Test
    void deveLancarExcecaoQuandoCepInvalidoOuVazioAtravesDeCadastro() {
        ClienteDtoRequest clienteComCepInvalido = new ClienteDtoRequest(
                "Maria Silva", "987.654.321-09", "maria@example.com", "", // CEP vazio
                "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> clienteService.cadastrarCliente(clienteComCepInvalido));

        assertEquals("CEP inválido ou vazio. Deve estar no formato XXXXX-XXX.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoCepNaoSegueFormato() {
        ClienteDtoRequest clienteComCepInvalido = new ClienteDtoRequest(
                "Maria Silva", "987.654.321-09", "maria@example.com", "1234567", // CEP no formato errado
                "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> clienteService.cadastrarCliente(clienteComCepInvalido));

        assertEquals("CEP inválido ou vazio. Deve estar no formato XXXXX-XXX.", exception.getMessage());
    }

    @Test
    void naoDeveCriarClienteSemLogradouroInformado() {
        ClienteDtoRequest dto = new ClienteDtoRequest("Maria Silva", "123.456.789-01", "maria@example.com", "12345-678", "", "100", "", "Centro", "São Paulo", "SP", "(11) 98765-4321");

        assertThrows(BusinessException.class, () -> clienteService.cadastrarCliente(dto), "Logradouro não pode ser vazio.");
    }

    @Test
    void naoDeveCriarClienteSemEstadoInformado() {
        ClienteDtoRequest dto = new ClienteDtoRequest("Maria Silva", "123.456.789-01", "maria@example.com", "12345-678", "Rua Sol", "100", "", "Centro", "São Paulo", "", "(11) 98765-4321");

        assertThrows(BusinessException.class, () -> clienteService.cadastrarCliente(dto), "Estado não pode ser vazio.");
    }

    @Test
    void naoDeveCriarClienteSemCidadeInformada() {
        ClienteDtoRequest dto = new ClienteDtoRequest("Maria Silva", "123.456.789-01", "maria@example.com", "12345-678", "Rua Sol", "100", "", "Centro", "", "SP", "(11) 98765-4321");

        assertThrows(BusinessException.class, () -> clienteService.cadastrarCliente(dto), "Cidade não pode ser vazia.");
    }

    @Test
    void naoDeveCriarClienteSemBairroInformado() {
        ClienteDtoRequest dto = new ClienteDtoRequest("Maria Silva", "123.456.789-01", "maria@example.com", "12345-678", "Rua Sol", "100", "", "", "São Paulo", "SP", "(11) 98765-4321");

        assertThrows(BusinessException.class, () -> clienteService.cadastrarCliente(dto), "Bairro não pode ser vazio.");
    }

    @Test
    void naoDeveCriarClienteSemNumeroInformado() {
        ClienteDtoRequest dto = new ClienteDtoRequest("Maria Silva", "123.456.789-01", "maria@example.com", "12345-678", "Rua Sol", "", "", "Centro", "São Paulo", "SP", "(11) 98765-4321");

        assertThrows(BusinessException.class, () -> clienteService.cadastrarCliente(dto), "Número não pode ser vazio.");
    }

    @Test
    void naoDeveCriarClienteSemTelefoneInformado() {
        ClienteDtoRequest dto = new ClienteDtoRequest("Maria Silva", "123.456.789-01", "maria@example.com", "12345-678", "Rua Sol", "100", "", "Centro", "São Paulo", "SP", "");

        assertThrows(BusinessException.class, () -> clienteService.cadastrarCliente(dto), "Telefone inválido ou vazio. Deve estar no formato (XX) 9XXXX-XXXX.");
    }

    @Test
    void deveLancarExcecaoQuandoTelefoneInvalidoOuVazio() {
        ClienteDtoRequest clienteComTelefoneInvalido = new ClienteDtoRequest(
                "Maria Silva", "987.654.321-09", "maria@example.com", "12345-678",
                "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", ""); // Telefone vazio

        BusinessException exception = assertThrows(BusinessException.class,
                () -> clienteService.cadastrarCliente(clienteComTelefoneInvalido));

        assertEquals("Telefone inválido ou vazio. Deve estar no formato (XX) 9XXXX-XXXX.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoFormatoDeTelefoneIncorreto() {
        ClienteDtoRequest clienteComTelefoneInvalido = new ClienteDtoRequest(
                "Maria Silva", "987.654.321-09", "maria@example.com", "12345-678",
                "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "21987654321"); // Formato de telefone incorreto

        BusinessException exception = assertThrows(BusinessException.class,
                () -> clienteService.cadastrarCliente(clienteComTelefoneInvalido));

        assertEquals("Telefone inválido ou vazio. Deve estar no formato (XX) 9XXXX-XXXX.", exception.getMessage());
    }

    @Test
    void deveCadastrarClienteComInformacoesValidas() throws BusinessException {
        ClienteDtoRequest dto = new ClienteDtoRequest("Maria Silva", "123.456.789-01", "maria@example.com", "12345-678", "Rua Sol", "100", "", "Centro", "São Paulo", "SP", "(11) 98765-4321");

        when(clienteRepository.save(any(ClienteEntity.class))).thenReturn(new ClienteEntity());
        assertNotNull(clienteService.cadastrarCliente(dto));
        verify(clienteRepository).save(any(ClienteEntity.class));
    }

    @Test
    void deveRetornarListaDeClientes() {
        // Configuração
        ClienteEntity cliente1 = new ClienteEntity(1L, "Maria Silva", "123.456.789-01", "maria@example.com", "12345-678", "Rua Sol", "100", "", "Centro", "São Paulo", "SP", "(11) 98765-4321");
        ClienteEntity cliente2 = new ClienteEntity(2L, "João Costa", "987.654.321-09", "joao@example.com", "87654-321", "Rua Lua", "200", "Apt 2", "Bairro Lunar", "Rio de Janeiro", "RJ", "(21) 65432-1987");

        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente1, cliente2));

        // Ação
        List<ClienteDtoResponse> resultados = clienteService.listarClientes();

        // Verificação
        assertNotNull(resultados);
        assertEquals(2, resultados.size());
        verify(clienteRepository).findAll();
        assertEquals("Maria Silva", resultados.get(0).nome());
        assertEquals("João Costa", resultados.get(1).nome());
    }

    @Test
    void deveAtualizarClienteComInformacoesValidas() throws BusinessException {
        ClienteDtoRequest dto = new ClienteDtoRequest("Maria Silva", "987.654.321-09", "maria@example.com", "87654-321", "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321");

        when(clienteRepository.save(any(ClienteEntity.class))).thenReturn(clienteExistente);

        ClienteDtoResponse updatedDto = clienteService.atualizarCliente(dto, clienteExistente);

        assertNotNull(updatedDto);
        assertEquals("Maria Silva", clienteExistente.getNome());
        assertEquals("987.654.321-09", clienteExistente.getCpf());
        assertEquals("maria@example.com", clienteExistente.getEmail());
        assertEquals("87654-321", clienteExistente.getCep());
        assertEquals("Rua Nova", clienteExistente.getLogradouro());
        assertEquals("101", clienteExistente.getNumero());
        assertEquals("Apto 2", clienteExistente.getComplemento());
        assertEquals("Bairro Novo", clienteExistente.getBairro());
        assertEquals("Cidade Nova", clienteExistente.getCidade());
        assertEquals("RJ", clienteExistente.getEstado());
        assertEquals("(21) 98765-4321", clienteExistente.getTelefone());
        verify(clienteRepository).save(clienteExistente);
    }

    @Test
    void deveAtualizarClientePorCodigo() throws BusinessException {
        // Criação do DTO de entrada
        ClienteDtoRequest clienteDto = new ClienteDtoRequest("Maria Silva", "987.654.321-09", "maria@example.com", "87654-321", "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321");

        // Cliente existente que será encontrado pelo código
        ClienteEntity clienteExistente = new ClienteEntity(1L, "João Silva", "123.456.789-00", "joao@example.com", "12345-678", "Rua A", "100", "Apto 1", "Bairro B", "Cidade C", "SP", "(11) 91234-5678");

        // Simulação do comportamento do repository para encontrar um cliente pelo código
        when(clienteRepository.findByCodigoCliente(1L)).thenReturn(clienteExistente);

        // Simulação do método 'save' do repository
        when(clienteRepository.save(any(ClienteEntity.class))).thenReturn(clienteExistente);

        // Ação: chamar o método 'atualizarClientePorCodigo'
        ClienteDtoResponse updatedDto = clienteService.atualizarClientePorCodigo(1L, clienteDto);

        // Verificações
        assertNotNull(updatedDto);
        assertEquals("Maria Silva", clienteExistente.getNome());
        assertEquals("987.654.321-09", clienteExistente.getCpf());
        assertEquals("maria@example.com", clienteExistente.getEmail());
        assertEquals("87654-321", clienteExistente.getCep());
        assertEquals("Rua Nova", clienteExistente.getLogradouro());
        assertEquals("101", clienteExistente.getNumero());
        assertEquals("Apto 2", clienteExistente.getComplemento());
        assertEquals("Bairro Novo", clienteExistente.getBairro());
        assertEquals("Cidade Nova", clienteExistente.getCidade());
        assertEquals("RJ", clienteExistente.getEstado());
        assertEquals("(21) 98765-4321", clienteExistente.getTelefone());

        // Verificação da interação com o repositório
        verify(clienteRepository).findByCodigoCliente(1L);
        verify(clienteRepository).save(clienteExistente);
    }

    @Test
    void deveAtualizarClientePorEmail() throws BusinessException {
        // Dados de entrada
        String email = "maria@example.com";
        ClienteDtoRequest clienteDto = new ClienteDtoRequest("Maria Silva", "987.654.321-09", "maria@example.com", "87654-321", "Rua Nova", "101", "Apto 2", "Bairro Novo", "Cidade Nova", "RJ", "(21) 98765-4321");

        // Cliente existente que será encontrado pelo email
        ClienteEntity clienteExistente = new ClienteEntity(1L, "João Silva", "123.456.789-00", "joao@example.com", "12345-678", "Rua A", "100", "Apto 1", "Bairro B", "Cidade C", "SP", "(11) 91234-5678");

        // Simulando o comportamento do repositório
        when(clienteRepository.findByEmail(email)).thenReturn(clienteExistente);
        when(clienteRepository.save(any(ClienteEntity.class))).thenReturn(clienteExistente);

        // Ação: chamando o método para atualizar o cliente pelo email
        ClienteDtoResponse updatedDto = clienteService.atualizarClientePorEmail(email, clienteDto);

        // Verificações
        assertNotNull(updatedDto);
        assertEquals("Maria Silva", updatedDto.nome());
        assertEquals("987.654.321-09", updatedDto.cpf());
        assertEquals("maria@example.com", updatedDto.email());
        assertEquals("87654-321", updatedDto.cep());
        assertEquals("Rua Nova", updatedDto.logradouro());
        assertEquals("101", updatedDto.numero());
        assertEquals("Apto 2", updatedDto.complemento());
        assertEquals("Bairro Novo", updatedDto.bairro());
        assertEquals("Cidade Nova", updatedDto.cidade());
        assertEquals("RJ", updatedDto.estado());
        assertEquals("(21) 98765-4321", updatedDto.telefone());

        // Verificando interações com o mock
        verify(clienteRepository).findByEmail(email);
        verify(clienteRepository).save(clienteExistente);
    }

    @Test
    void naoDeveExcluirClientePorCodigoInexistente() {
        // Dados de entrada
        Long codigoCliente = 1L;

        // Simulando o comportamento do repositório para não encontrar o cliente
        when(clienteRepository.findByCodigoCliente(codigoCliente)).thenReturn(null);

        // Ação e Verificação: esperando uma exceção ao tentar excluir um cliente inexistente
        assertThrows(BusinessException.class, () -> clienteService.excluirClientePorCodigo(codigoCliente));

        // Verificando que não foi tentada a exclusão no repositório
        verify(clienteRepository, never()).deleteById(codigoCliente);
    }

    @Test
    void deveExcluirClientePorCodigo() throws BusinessException {
        // Dados de entrada
        Long codigoCliente = 1L;

        // Cliente que será encontrado para exclusão
        ClienteEntity clienteMock = new ClienteEntity();
        clienteMock.setCodigoCliente(codigoCliente);

        // Simulando o comportamento do repositório
        when(clienteRepository.findByCodigoCliente(codigoCliente)).thenReturn(clienteMock);
        when(consumerFeignClient.clientePossuiPedidos(codigoCliente)).thenReturn(Map.of("possui-pedidos", false));
        // Ação: chamando o método para excluir o cliente pelo código
        clienteService.excluirClientePorCodigo(codigoCliente);

        // Verificando as interações com o mock
        verify(clienteRepository).findByCodigoCliente(codigoCliente);
        verify(clienteRepository).delete(any());
    }

    @Test
    void naoDeveExcluirClientePorEmailInexistente() {
        // Dados de entrada
        String email = "maria@example.com";

        // Simulando o comportamento do repositório para não encontrar o cliente
        when(clienteRepository.findByEmail(email)).thenReturn(null);

        // Ação e Verificação: esperando uma exceção ao tentar excluir um cliente inexistente
        assertThrows(BusinessException.class, () -> clienteService.excluirClientePorEmail(email));

        // Verificando que não foi tentada a exclusão no repositório
        verify(clienteRepository, never()).delete(any(ClienteEntity.class));
    }

    @Test
    void deveExcluirClientePorEmail() throws BusinessException {
        // Dados de entrada
        String email = "maria@example.com";

        // Cliente que será encontrado para exclusão
        ClienteEntity clienteMock = new ClienteEntity();
        clienteMock.setEmail(email);

        // Simulando o comportamento do repositório
        when(clienteRepository.findByEmail(email)).thenReturn(clienteMock);
        when(consumerFeignClient.clientePossuiPedidos(any())).thenReturn(Map.of("possui-pedidos", false));
        // Ação: chamando o método para excluir o cliente pelo email
        clienteService.excluirClientePorEmail(email);

        // Verificando as interações com o mock
        verify(clienteRepository).findByEmail(email);
        verify(clienteRepository).delete(clienteMock);
    }
}

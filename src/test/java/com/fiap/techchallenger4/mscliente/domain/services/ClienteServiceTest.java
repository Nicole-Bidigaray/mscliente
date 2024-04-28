package com.fiap.techchallenger4.mscliente.domain.services;

import br.com.fiap.estrutura.exception.BusinessException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

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
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        // Cliente já existe com o Email
        ClienteDtoRequest novoCliente = new ClienteDtoRequest("Maria Silva", "123.456.789-01", "maria@already.com", "12345-678", "Rua Sol", "100", "", "Centro", "São Paulo", "SP", "(11) 98765-4321");
        when(clienteRepository.existsByEmail(novoCliente.email())).thenReturn(true);

        // Ação e Verificação
        BusinessException exception = assertThrows(BusinessException.class, () -> clienteService.cadastrarCliente(novoCliente));
        assertEquals("Email já cadastrado.", exception.getMessage());
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

        // Ação: chamando o método para excluir o cliente pelo código
        clienteService.excluirClientePorCodigo(codigoCliente);

        // Verificando as interações com o mock
        verify(clienteRepository).findByCodigoCliente(codigoCliente);
        verify(clienteRepository).deleteById(codigoCliente);
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

        // Ação: chamando o método para excluir o cliente pelo email
        clienteService.excluirClientePorEmail(email);

        // Verificando as interações com o mock
        verify(clienteRepository).findByEmail(email);
        verify(clienteRepository).delete(clienteMock);
    }
}

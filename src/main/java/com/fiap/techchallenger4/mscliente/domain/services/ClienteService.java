package com.fiap.techchallenger4.mscliente.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiap.techchallenger4.mscliente.domain.consumer.PedidoConsumerFeignClient;
import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoRequest;
import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoResponse;
import com.fiap.techchallenger4.mscliente.domain.entities.ClienteEntity;
import com.fiap.techchallenger4.mscliente.domain.repositories.ClienteRepository;

import br.com.fiap.estrutura.exception.BusinessException;
import br.com.fiap.estrutura.exception.EntidadeNaoEncontrada;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PedidoConsumerFeignClient pedidoConsumerFeignClient;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, PedidoConsumerFeignClient pedidoConsumerFeignClient) {
		this.clienteRepository = clienteRepository;
		this.pedidoConsumerFeignClient = pedidoConsumerFeignClient;
	}

	private ClienteEntity findByCodigoCliente(Long codigoCliente) throws BusinessException {
        ClienteEntity cliente = clienteRepository.findByCodigoCliente(codigoCliente);
        if (cliente == null) {
            throw new EntidadeNaoEncontrada("Cliente com código " + codigoCliente + " não encontrado");
        }
        return cliente;
    }

    private ClienteEntity findByEmail(String email) throws BusinessException {
        ClienteEntity cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            throw new EntidadeNaoEncontrada("Cliente com Email " + email + " não encontrado");
        }
        return cliente;
    }

    private void verificaDuplicidade(ClienteDtoRequest dto, ClienteEntity clienteExistente) throws BusinessException {
        if (clienteExistente != null) {
            if (!clienteExistente.getCpf().equals(dto.cpf()) && clienteRepository.existsByCpf(dto.cpf())) {
                throw new BusinessException("CPF já cadastrado.");
            }
            if (!clienteExistente.getEmail().equals(dto.email()) && clienteRepository.existsByEmail(dto.email())) {
                throw new BusinessException("Email já cadastrado.");
            }
        } else {
            if (clienteRepository.existsByCpf(dto.cpf())) {
                throw new BusinessException("CPF já cadastrado.");
            }
            if (clienteRepository.existsByEmail(dto.email())) {
                throw new BusinessException("Email já cadastrado.");
            }
        }
    }

    private void validarEndereco(ClienteDtoRequest dto) throws BusinessException {
        if (dto.cep().isBlank() || !dto.cep().matches("\\d{5}-\\d{3}"))
            throw new BusinessException("CEP inválido ou vazio. Deve estar no formato XXXXX-XXX.");
        if (dto.logradouro().isBlank()) throw new BusinessException("Logradouro não pode ser vazio.");
        if (dto.numero().isBlank()) throw new BusinessException("Número não pode ser vazio.");
        if (dto.bairro().isBlank()) throw new BusinessException("Bairro não pode ser vazio.");
        if (dto.cidade().isBlank()) throw new BusinessException("Cidade não pode ser vazia.");
        if (dto.estado().isBlank()) throw new BusinessException("Estado não pode ser vazio.");
        if (dto.telefone().isBlank() || !dto.telefone().matches("\\(\\d{2}\\) 9?\\d{4}-\\d{4}"))
            throw new BusinessException("Telefone inválido ou vazio. Deve estar no formato (XX) 9XXXX-XXXX.");
    }

    private void validarClienteDto(ClienteDtoRequest dto, ClienteEntity clienteExistente) throws BusinessException {
        if (dto.nome().isBlank()) throw new BusinessException("Nome não pode ser vazio.");
        if (dto.cpf().isBlank() || !dto.cpf().matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}"))
            throw new BusinessException("CPF inválido ou vazio. Deve estar no formato XXX.XXX.XXX-XX.");
        if (dto.email().isBlank() || !dto.email().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"))
            throw new BusinessException("Email inválido ou vazio. Formato esperado: exemplo@dominio.com");

        verificaDuplicidade(dto, clienteExistente);

        validarEndereco(dto);
    }

    public List<ClienteDtoResponse> listarClientes() {
        return clienteRepository.findAll().stream().map(ClienteEntity::toDto).toList();
    }

    public ClienteDtoResponse buscarClientePorCodigo(Long codigoCliente) throws BusinessException {
        ClienteEntity cliente = findByCodigoCliente(codigoCliente);
        return cliente.toDto();
    }

    public ClienteDtoResponse buscarClientePorEmail(String email) throws BusinessException {
        ClienteEntity cliente = findByEmail(email);
        return cliente.toDto();
    }

    public ClienteDtoResponse cadastrarCliente(ClienteDtoRequest cliente) throws BusinessException {
        validarClienteDto(cliente, null);
        ClienteEntity novoCliente = cliente.toEntity();
        ClienteEntity clienteSalvo = clienteRepository.save(novoCliente);
        return clienteSalvo.toDto();
    }

    public ClienteDtoResponse atualizarCliente(ClienteDtoRequest clienteDto, ClienteEntity clienteExistente) throws BusinessException {
        validarClienteDto(clienteDto, clienteExistente);

        // Atualizar diretamente o clienteExistente em vez de criar um novo.
        clienteExistente.setNome(clienteDto.nome());
        clienteExistente.setCpf(clienteDto.cpf());
        clienteExistente.setEmail(clienteDto.email());
        clienteExistente.setCep(clienteDto.cep());
        clienteExistente.setLogradouro(clienteDto.logradouro());
        clienteExistente.setNumero(clienteDto.numero());
        clienteExistente.setComplemento(clienteDto.complemento());
        clienteExistente.setBairro(clienteDto.bairro());
        clienteExistente.setCidade(clienteDto.cidade());
        clienteExistente.setEstado(clienteDto.estado());
        clienteExistente.setTelefone(clienteDto.telefone());

        clienteRepository.save(clienteExistente);
        return clienteExistente.toDto();
    }

    public ClienteDtoResponse atualizarClientePorCodigo(Long codigoCliente, ClienteDtoRequest clienteDto) throws BusinessException {
        ClienteEntity clienteExistente = findByCodigoCliente(codigoCliente);
        return atualizarCliente(clienteDto, clienteExistente);
    }

    public ClienteDtoResponse atualizarClientePorEmail(String email, ClienteDtoRequest clienteDto) throws BusinessException {
        ClienteEntity clienteExistente = findByEmail(email);
        return atualizarCliente(clienteDto, clienteExistente);
    }

    public void excluirClientePorCodigo(Long codigoCliente) throws BusinessException {
        ClienteEntity cliente = findByCodigoCliente(codigoCliente);
        
        validarSeClientePossuiPedidos(codigoCliente);
        
        clienteRepository.delete(cliente);
    }

	private void validarSeClientePossuiPedidos(Long codigoCliente) throws BusinessException {
		if(pedidoConsumerFeignClient.clientePossuiPedidos(codigoCliente).get("possui-pedidos")) {
        	throw new BusinessException("O Cliente não pode ser excluido pois possui pedidos realizados");
        }
	}

    public void excluirClientePorEmail(String email) throws BusinessException {
        ClienteEntity cliente = findByEmail(email);
        
        validarSeClientePossuiPedidos(cliente.getCodigoCliente());
        
        clienteRepository.delete(cliente);
    }
}

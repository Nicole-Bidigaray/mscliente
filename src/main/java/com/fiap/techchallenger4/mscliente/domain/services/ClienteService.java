package com.fiap.techchallenger4.mscliente.domain.services;

import br.com.fiap.estrutura.exception.BusinessException;
import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoRequest;
import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoResponse;
import com.fiap.techchallenger4.mscliente.domain.entities.ClienteEntity;
import com.fiap.techchallenger4.mscliente.domain.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    private ClienteEntity buscarClienteEntity(Long codigoCliente) throws BusinessException {
        ClienteEntity cliente = clienteRepository.findByCodigoCliente(codigoCliente);
        if (cliente == null) {
            throw new BusinessException("Cliente não encontrado");
        }
        return cliente;
    }

    private void validarClienteDto(ClienteDtoRequest dto) throws BusinessException {
        if (dto.nome().isBlank()) {
            throw new BusinessException("Nome não pode ser vazio.");
        }
        if (dto.cpf().isBlank() || !dto.cpf().matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new BusinessException("CPF inválido ou vazio. Deve estar no formato XXX.XXX.XXX-XX.");
        }
        if (clienteRepository.existsByCpf(dto.cpf())) {
            throw new BusinessException("CPF já cadastrado.");
        }
        if (dto.email().isBlank() || !dto.email().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new BusinessException("Email inválido ou vazio. Formato esperado: exemplo@dominio.com");
        }
        if (clienteRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Email já cadastrado.");
        }
        if (dto.cep().isBlank() || !dto.cep().matches("\\d{5}-\\d{3}")) {
            throw new BusinessException("CEP inválido ou vazio. Deve estar no formato XXXXX-XXX.");
        }
        if (dto.logradouro().isBlank()) {
            throw new BusinessException("Logradouro não pode ser vazio.");
        }
        if (dto.numero().isBlank()) {
            throw new BusinessException("Número não pode ser vazio.");
        }
        if (dto.bairro().isBlank()) {
            throw new BusinessException("Bairro não pode ser vazio.");
        }
        if (dto.cidade().isBlank()) {
            throw new BusinessException("Cidade não pode ser vazia.");
        }
        if (dto.estado().isBlank()) {
            throw new BusinessException("Estado não pode ser vazio.");
        }
        if (dto.telefone().isBlank() || !dto.telefone().matches("\\(\\d{2}\\) 9?\\d{4}-\\d{4}")) {
            throw new BusinessException("Telefone inválido ou vazio. Deve estar no formato (XX) 9XXXX-XXXX.");
        }
    }

    public List<ClienteDtoResponse> listarClientes() {
        return clienteRepository.findAll().stream().map(ClienteEntity::toDto).toList();
    }

    public ClienteDtoResponse buscarClientePorCodigo(Long codigoCliente) throws BusinessException {
        ClienteEntity cliente = buscarClienteEntity(codigoCliente);
        return cliente.toDto();
    }

    public ClienteDtoResponse cadastrarCliente(ClienteDtoRequest cliente) throws BusinessException {
        validarClienteDto(cliente);
        ClienteEntity novoCliente = cliente.toEntity();
        ClienteEntity clienteSalvo = clienteRepository.save(novoCliente);
        return clienteSalvo.toDto();
    }

    public ClienteDtoResponse atualizarCliente(Long codigoCliente, ClienteDtoRequest clienteDto) throws BusinessException {
        validarClienteDto(clienteDto);
        ClienteEntity clienteExistente = buscarClienteEntity(codigoCliente);
        if (!clienteExistente.getCpf().equals(clienteDto.cpf()) && clienteRepository.existsByCpf(clienteDto.cpf())) {
            throw new BusinessException("CPF já cadastrado em outro registro.");
        }
        if (!clienteExistente.getEmail().equals(clienteDto.email()) && clienteRepository.existsByEmail(clienteDto.email())) {
            throw new BusinessException("Email já cadastrado em outro registro.");
        }

        ClienteEntity clienteAtualizado = new ClienteEntity(
            clienteExistente.getCodigoCliente(),
            clienteDto.nome(),
            clienteDto.cpf(),
            clienteDto.email(),
            clienteDto.cep(),
            clienteDto.logradouro(),
            clienteDto.numero(),
            clienteDto.complemento(),
            clienteDto.bairro(),
            clienteDto.cidade(),
            clienteDto.estado(),
            clienteDto.telefone()
        );
        return clienteRepository.save(clienteAtualizado).toDto();
    }

    public void excluirCliente(Long codigoCliente) throws BusinessException {
        buscarClienteEntity(codigoCliente);
        clienteRepository.deleteById(codigoCliente);
    }

}

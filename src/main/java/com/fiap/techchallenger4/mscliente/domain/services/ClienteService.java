package com.fiap.techchallenger4.mscliente.domain.services;

import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoRequest;
import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoResponse;
import com.fiap.techchallenger4.mscliente.domain.entities.ClienteEntity;
import com.fiap.techchallenger4.mscliente.domain.exceptions.BusinessException;
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

    public List<ClienteDtoResponse> listarClientes() {
        return clienteRepository.findAll().stream().map(ClienteEntity::toDto).toList();
    }

    public ClienteDtoResponse buscarClientePorCodigo(Long codigoCliente) throws BusinessException {
        ClienteEntity cliente = buscarClienteEntity(codigoCliente);
        return cliente.toDto();
    }

    public ClienteDtoResponse cadastrarCliente(ClienteDtoRequest cliente) throws BusinessException {
        ClienteEntity clienteExistente = clienteRepository.findByCpf(cliente.cpf());
        if (clienteExistente != null) {
            throw new BusinessException("Já existe um cliente cadastrado com este CPF");
        }
        return clienteRepository.save(cliente.toEntity()).toDto();
    }

    public ClienteDtoResponse atualizarCliente(Long codigoCliente, ClienteDtoRequest clienteDto) throws BusinessException {
        ClienteEntity clienteExistente = buscarClienteEntity(codigoCliente);

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

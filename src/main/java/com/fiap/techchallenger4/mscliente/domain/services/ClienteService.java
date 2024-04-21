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

    private ClienteEntity buscarClienteEntity(Long id) throws BusinessException {
        return clienteRepository.findById(id).orElseThrow(() -> new BusinessException("Cliente não encontrado"));
    }

    public List<ClienteDtoResponse> listarClientes() {
        return clienteRepository.findAll().stream().map(ClienteEntity::toDto).toList();
    }

    public ClienteDtoResponse buscarClientePorId(Long id) throws BusinessException {
        ClienteEntity cliente = buscarClienteEntity(id);
        return cliente.toDto();
    }

    public ClienteDtoResponse cadastrarCliente(ClienteDtoRequest cliente) throws BusinessException {
        ClienteEntity clienteExistente = clienteRepository.findByCpf(cliente.cpf());
        if (clienteExistente != null) {
            throw new BusinessException("Já existe um cliente cadastrado com este CPF");
        }
        return clienteRepository.save(cliente.toEntity()).toDto();
    }

    public ClienteDtoResponse atualizarCliente(long codigoCliente, ClienteDtoRequest clienteDto) throws BusinessException {
        ClienteEntity clienteExistente = buscarClienteEntity(codigoCliente);

        ClienteEntity clienteAtualizado = new ClienteEntity(
                clienteExistente.getCodigoCliente(),
                clienteDto.nome(),
                clienteDto.cpf(),
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

    public void excluirCliente(Long id) throws BusinessException {
        buscarClienteEntity(id);
        clienteRepository.deleteById(id);
    }

}

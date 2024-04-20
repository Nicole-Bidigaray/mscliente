package com.fiap.techchallenger4.mscliente.domain.dto;

import com.fiap.techchallenger4.mscliente.domain.entities.ClienteEntity;
import com.fiap.techchallenger4.mscliente.domain.exceptions.BusinessException;

public record ClienteDtoRequest(
        String nome,
        String cpf,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String telefone
) {
    public ClienteEntity toEntity() throws BusinessException {
        return new ClienteEntity(nome, cpf, cep, logradouro, numero, complemento, bairro, cidade, estado, telefone);
    }
}

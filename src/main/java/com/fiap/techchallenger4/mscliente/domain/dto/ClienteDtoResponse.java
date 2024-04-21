package com.fiap.techchallenger4.mscliente.domain.dto;

public record ClienteDtoResponse (
        Long codigoCliente,
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
}

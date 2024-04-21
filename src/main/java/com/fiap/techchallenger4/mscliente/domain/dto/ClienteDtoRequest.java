package com.fiap.techchallenger4.mscliente.domain.dto;

import com.fiap.techchallenger4.mscliente.domain.entities.ClienteEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClienteDtoRequest(

        @NotBlank
        String nome,

        @NotBlank @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")
        String cpf,

        @NotBlank @Email
        String email,

        @NotBlank @Pattern(regexp = "\\d{5}-\\d{3}")
        String cep,

        @NotBlank
        String logradouro,

        @NotBlank
        String numero,

        String complemento,

        @NotBlank
        String bairro,

        @NotBlank
        String cidade,

        @NotBlank
        String estado,

        @NotBlank @Pattern(regexp = "\\(\\d{2}\\) 9?\\d{4}-\\d{4}")
        String telefone
) {
    public ClienteEntity toEntity() {
        return new ClienteEntity(null, nome, cpf, email, cep, logradouro, numero, complemento, bairro, cidade, estado, telefone);
    }
}

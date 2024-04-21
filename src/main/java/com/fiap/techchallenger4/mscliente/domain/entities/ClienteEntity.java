package com.fiap.techchallenger4.mscliente.domain.entities;

import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigoCliente;

    private String nome;
    private String cpf;
    private String email;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String telefone;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    public ClienteEntity(Long codigoCliente, String nome, String cpf, String email, String cep, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String telefone) {
        this.codigoCliente = codigoCliente;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.telefone = telefone;
    }

    public ClienteDtoResponse toDto() {
        return new ClienteDtoResponse(codigoCliente, nome, cpf, email, cep, logradouro, numero, complemento, bairro, cidade, estado, telefone, dataCriacao);
    }
}

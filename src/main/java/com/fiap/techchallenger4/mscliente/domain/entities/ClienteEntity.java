package com.fiap.techchallenger4.mscliente.domain.entities;

import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoResponse;
import com.fiap.techchallenger4.mscliente.domain.exceptions.BusinessException;
import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
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

    public ClienteEntity(){}

    public ClienteEntity(Long codigoCliente, String nome, String email, String cpf, String cep, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String telefone) {
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

    public Long getCodigoCliente() {
        return codigoCliente;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCep() {
        return cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getTelefone() {
        return telefone;
    }

    public ClienteDtoResponse toDto() {
        return new ClienteDtoResponse(codigoCliente, nome, cpf, email, cep, logradouro, numero, complemento, bairro, cidade, estado, telefone);
    }
}

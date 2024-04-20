package com.fiap.techchallenger4.mscliente.domain.entities;

import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoResponse;
import com.fiap.techchallenger4.mscliente.domain.exceptions.BusinessException;
import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cpf;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String telefone;

    public ClienteEntity(){}

    public ClienteEntity(String nome, String cpf, String cep, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String telefone) throws BusinessException {
        validarDados(nome, cpf, cep, logradouro, numero, complemento, bairro, cidade, estado, telefone);
        this.nome = nome;
        this.cpf = cpf;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.telefone = telefone;
    }


    public ClienteEntity(Long id, String nome, String cpf, String cep, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String telefone) throws BusinessException {
        validarDados(nome, cpf, cep, logradouro, numero, complemento, bairro, cidade, estado, telefone);
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
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
        return new ClienteDtoResponse(id, nome, cpf, cep, logradouro, numero, complemento, bairro, cidade, estado, telefone);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
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

    private boolean cpfValido(String cpf) {
        return cpf.matches("[0-9]{11}");
    }

    private boolean cepValido(String cep) {
        return cep.matches("[0-9]{8}");
    }

    private boolean telefoneValido(String telefone) {
        return telefone.matches("\\([1-9]{2}\\) [9]{0,1}[0-9]{4}-[0-9]{4}");
    }

    private void validarDados(String nome, String cpf, String cep, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String telefone) throws BusinessException {
        if (nome == null || nome.isBlank()) {
            throw new BusinessException("Nome não pode ser vazio");
        }
        if (cpf == null || cpf.isBlank() || !cpfValido(cpf)) {
            throw new BusinessException("CPF inválido");
        }
        if (cep == null || cep.isBlank() || !cepValido(cep)) {
            throw new BusinessException("CEP inválido");
        }
        if (logradouro == null || logradouro.isBlank()) {
            throw new BusinessException("Logradouro não pode ser vazio");
        }
        if (numero == null || numero.isBlank()) {
            throw new BusinessException("Número não pode ser vazio");
        }
        if (bairro == null || bairro.isBlank()) {
            throw new BusinessException("Bairro não pode ser vazio");
        }
        if (cidade == null || cidade.isBlank()) {
            throw new BusinessException("Cidade não pode ser vazio");
        }
        if (estado == null || estado.isBlank()) {
            throw new BusinessException("Estado não pode ser vazio");
        }
        if (telefone == null || telefone.isBlank() || !telefoneValido(telefone)) {
            throw new BusinessException("Telefone inválido");
        }
    }

}

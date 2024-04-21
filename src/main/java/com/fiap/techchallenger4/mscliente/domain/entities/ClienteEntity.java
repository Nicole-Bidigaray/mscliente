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

    public ClienteEntity(Long codigoCliente, String nome, String email, String cpf, String cep, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String telefone) throws BusinessException {
        validarDados(nome, cpf, email, cep, logradouro, numero, complemento, bairro, cidade, estado, telefone);
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
        return new ClienteDtoResponse(codigoCliente, nome, cpf, email, cep, logradouro, numero, complemento, bairro, cidade, estado, telefone);
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
        return cpf.matches("\\\\d{3}\\\\.\\\\d{3}\\\\.\\\\d{3}-\\\\d{2}");
    }

    private boolean emailValido(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    private boolean cepValido(String cep) {
        return cep.matches("\\\\d{5}-\\\\d{3}");
    }

    private boolean telefoneValido(String telefone) {
        return telefone.matches("\\(\\d{2}\\) 9?\\d{4}-\\d{4}");
    }

    private void validarDados(String nome, String cpf, String email, String cep, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String telefone) throws BusinessException {
        validarNome(nome);
        validarCPF(cpf);
        validarEmail(email);
        validarCEP(cep);
        validarLogradouro(logradouro);
        validarNumero(numero);
        validarBairro(bairro);
        validarCidade(cidade);
        validarEstado(estado);
        validarTelefone(telefone);
    }

    private void validarNome(String nome) throws BusinessException {
        if (nome == null || nome.isBlank()) {
            throw new BusinessException("Nome não pode ser vazio");
        }
    }

    private void validarCPF(String cpf) throws BusinessException {
        if (cpf == null || cpf.isBlank() || !cpfValido(cpf)) {
            throw new BusinessException("CPF inválido");
        }
    }

    private void validarEmail(String email) throws BusinessException {
        if (email == null || email.isBlank() || !emailValido(email)) {
            throw new BusinessException("Email inválido");
        }
    }

    private void validarCEP(String cep) throws BusinessException {
        if (cep == null || cep.isBlank() || !cepValido(cep)) {
            throw new BusinessException("CEP inválido");
        }
    }

    private void validarLogradouro(String logradouro) throws BusinessException {
        if (logradouro == null || logradouro.isBlank()) {
            throw new BusinessException("Logradouro não pode ser vazio");
        }
    }

    private void validarNumero(String numero) throws BusinessException {
        if (numero == null || numero.isBlank()) {
            throw new BusinessException("Número não pode ser vazio");
        }
    }

    private void validarBairro(String bairro) throws BusinessException {
        if (bairro == null || bairro.isBlank()) {
            throw new BusinessException("Bairro não pode ser vazio");
        }
    }

    private void validarCidade(String cidade) throws BusinessException {
        if (cidade == null || cidade.isBlank()) {
            throw new BusinessException("Cidade não pode ser vazio");
        }
    }

    private void validarEstado(String estado) throws BusinessException {
        if (estado == null || estado.isBlank()) {
            throw new BusinessException("Estado não pode ser vazio");
        }
    }

    private void validarTelefone(String telefone) throws BusinessException {
        if (telefone == null || telefone.isBlank() || !telefoneValido(telefone)) {
            throw new BusinessException("Telefone inválido");
        }
    }

}

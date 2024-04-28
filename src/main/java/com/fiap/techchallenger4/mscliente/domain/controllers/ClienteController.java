package com.fiap.techchallenger4.mscliente.domain.controllers;

import br.com.fiap.estrutura.swagger.annotations.ApiResponseSwaggerCreate;
import br.com.fiap.estrutura.swagger.annotations.ApiResponseSwaggerNoContent;
import br.com.fiap.estrutura.swagger.annotations.ApiResponseSwaggerOk;
import br.com.fiap.estrutura.utils.SpringControllerUtils;
import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoRequest;
import com.fiap.techchallenger4.mscliente.domain.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Clientes", description = "Rotas para gerenciamento dos clientes")
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    @Operation(summary = "Listar todos os clientes")
    @ApiResponseSwaggerOk
    public ResponseEntity<?> listarClientes() {
        return ResponseEntity.status(HttpStatus.OK).body(clienteService.listarClientes());
    }

    @GetMapping("/{codigoCliente}")
    @Operation(summary = "Buscar cliente por código")
    @ApiResponseSwaggerOk
    @ApiResponseSwaggerNoContent
    public ResponseEntity<?> buscarClientePorCodigo(@PathVariable Long codigoCliente) {
        return SpringControllerUtils.response(HttpStatus.OK, () -> clienteService.buscarClientePorCodigo(codigoCliente));
    }

    @GetMapping("cpf/{cpf}")
    @Operation(summary = "Buscar cliente por CPF")
    @ApiResponseSwaggerOk
    @ApiResponseSwaggerNoContent
    public ResponseEntity<?> buscarClientePorCpf(@PathVariable String cpf) {
        return SpringControllerUtils.response(HttpStatus.OK, () -> clienteService.buscarClientePorCpf(cpf));
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo cliente")
    @ApiResponseSwaggerCreate
    public ResponseEntity<?> cadastrarCliente(@RequestBody ClienteDtoRequest cliente) {
        return SpringControllerUtils.response(HttpStatus.CREATED, () -> clienteService.cadastrarCliente(cliente));
    }

    @PutMapping("/{codigoCliente}")
    @Operation(summary = "Atualizar um cliente pelo Código de Cliente")
    @ApiResponseSwaggerOk
    public ResponseEntity<?> atualizarClientePorCodigo(@PathVariable Long codigoCliente, @RequestBody ClienteDtoRequest clienteDto) {
        return SpringControllerUtils.response(HttpStatus.OK, () -> clienteService.atualizarClientePorCodigo(codigoCliente, clienteDto));
    }

    @PutMapping("cpf/{cpf}")
    @Operation(summary = "Atualizar um cliente pelo CPF")
    @ApiResponseSwaggerOk
    public ResponseEntity<?> atualizarClientePorCpf(@PathVariable String cpf, @RequestBody ClienteDtoRequest clienteDto) {
        return SpringControllerUtils.response(HttpStatus.OK, () -> clienteService.atualizarClientePorCpf(cpf, clienteDto));
    }

    @DeleteMapping("/{codigoCliente}")
    @Operation(summary = "Excluir um cliente pelo Código de Cliente")
    @ApiResponseSwaggerNoContent
    public ResponseEntity<?> excluirClientePorCodigo(@PathVariable Long codigoCliente) {
        return SpringControllerUtils.response(HttpStatus.NO_CONTENT, () -> {
            clienteService.excluirClientePorCodigo(codigoCliente);
            return null;
        });
    }

    @DeleteMapping("cpf/{cpf}")
    @Operation(summary = "Excluir um cliente pelo CPF")
    @ApiResponseSwaggerNoContent
    public ResponseEntity<?> excluirClientePorCpf(@PathVariable String cpf) {
        return SpringControllerUtils.response(HttpStatus.NO_CONTENT, () -> {
            clienteService.excluirClientePorCpf(cpf);
            return null;
        });
    }
}

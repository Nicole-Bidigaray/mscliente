package com.fiap.techchallenger4.mscliente.domain.controllers;

import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoRequest;
import com.fiap.techchallenger4.mscliente.domain.services.ClienteService;
import com.fiap.techchallenger4.mscliente.infra.Utils;
import com.fiap.techchallenger4.mscliente.infra.swagger.annotations.ApiResponseSwaggerCreate;
import com.fiap.techchallenger4.mscliente.infra.swagger.annotations.ApiResponseSwaggerNoContent;
import com.fiap.techchallenger4.mscliente.infra.swagger.annotations.ApiResponseSwaggerOk;
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
        return Utils.response(HttpStatus.OK, () -> clienteService.buscarClientePorCodigo(codigoCliente));
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo cliente")
    @ApiResponseSwaggerCreate
    public ResponseEntity<?> cadastrarCliente(@RequestBody ClienteDtoRequest cliente) {
        return Utils.response(HttpStatus.CREATED, () -> clienteService.cadastrarCliente(cliente));
    }

    @PutMapping("/{codigoCliente}")
    @Operation(summary = "Atualizar um cliente")
    @ApiResponseSwaggerOk
    public ResponseEntity<?> atualizarCliente(@PathVariable Long codigoCliente, @RequestBody ClienteDtoRequest clienteDto) {
        return Utils.response(HttpStatus.OK, () -> clienteService.atualizarCliente(codigoCliente, clienteDto));
    }

    @DeleteMapping("/{codigoCliente}")
    @Operation(summary = "Excluir um cliente")
    @ApiResponseSwaggerNoContent
    public ResponseEntity<?> excluirCliente(@PathVariable Long codigoCliente) {
        return Utils.response(HttpStatus.NO_CONTENT, () -> {
            clienteService.excluirCliente(codigoCliente);
            return null;
        });
    }
}

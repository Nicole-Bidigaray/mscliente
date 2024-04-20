package com.fiap.techchallenger4.mscliente.domain.controllers;

import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoRequest;
import com.fiap.techchallenger4.mscliente.domain.exceptions.BusinessException;
import com.fiap.techchallenger4.mscliente.domain.services.ClienteService;
import com.fiap.techchallenger4.mscliente.infra.handler.MessageErrorHandler;
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
        try {
            return ResponseEntity.status(HttpStatus.OK).body(clienteService.listarClientes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MessageErrorHandler.create(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    @ApiResponseSwaggerOk
    @ApiResponseSwaggerNoContent
    public ResponseEntity<?> buscarClientePorId(@PathVariable Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(clienteService.buscarClientePorId(id));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageErrorHandler.create(e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo cliente")
    @ApiResponseSwaggerCreate
    public ResponseEntity<?> cadastrarCliente(@RequestBody ClienteDtoRequest cliente) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.cadastrarCliente(cliente));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageErrorHandler.create(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um cliente")
    @ApiResponseSwaggerOk
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id, @RequestBody ClienteDtoRequest clienteDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(clienteService.atualizarCliente(id, clienteDto));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageErrorHandler.create(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um cliente")
    @ApiResponseSwaggerNoContent
    public ResponseEntity<?> excluirCliente(@PathVariable Long id) {
        try {
            clienteService.excluirCliente(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageErrorHandler.create(e.getMessage()));
        }
    }
}

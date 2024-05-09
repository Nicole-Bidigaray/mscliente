package com.fiap.techchallenger4.mscliente.domain.controllers;

import com.fiap.techchallenger4.mscliente.domain.dto.ClienteDtoRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Profile("local")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClienteControllerITTest {
    @LocalServerPort
    private int porta;

    @BeforeEach
    void setUp() {
        RestAssured.port = porta;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class BuscarClientes {
        @Test
        void deveListarClientes() {
            given()
                    .contentType(ContentType.JSON)
                    .when()
                        .get("/clientes")
                    .then()
                        .statusCode(HttpStatus.SC_OK);
        }

        @Test
        void deveBuscarClientePorCodigoCliente() {
            int expectedId = 5;
            given()
                    .pathParam("codigoCliente", expectedId)
            .when()
                    .get("/clientes/{codigoCliente}")
            .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("codigoCliente", equalTo(expectedId));
        }

        @Test
        void naoDeveBuscarClientePorCodigoInexistente() {
            int codigoClienteInexistente = 50000;
            given()
                    .pathParam("codigoCliente", codigoClienteInexistente)
            .when()
                    .get("/clientes/{codigoCliente}")
            .then()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("message", is("Cliente com código " + codigoClienteInexistente + " não encontrado"));
        }

        @Test
        void deveBuscarClientePorEmail() {
            String email = "pedro.santos@email.com";
            given()
                    .pathParam("email", email)
            .when()
                    .get("/clientes/email/{email}")
            .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("email", equalTo(email));
        }

        @Test
        void naoDeveBuscarClientePorEmailInexistente() {
            String emailInexistente = "inexistente@example.com";
            given()
                    .pathParam("email", emailInexistente)
            .when()
                    .get("/clientes/email/{email}")
            .then()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("message", equalTo("Cliente com Email " + emailInexistente + " não encontrado"));
        }
    }

    @Nested
    class CadastrarClientes {
        @Test
        void deveCadastrarClienteComDadosNovos() {
            // Prepara o corpo da requisição com dados novos e únicos
            ClienteDtoRequest novoCliente = new ClienteDtoRequest(
                    "Roberta Campos", "999.888.777-66", "roberta.campos@email.com", "98765-432",
                    "Avenida Nova", "200", "Casa 20", "Novo Bairro", "Curitiba", "PR", "(41) 98765-4321"
            );

            // Executa a requisição POST para cadastrar o cliente e verifica as respostas
            given()
                    .contentType(ContentType.JSON)
                    .body(novoCliente)
            .when()
                    .post("/clientes")
            .then()
                    .statusCode(HttpStatus.SC_CREATED) // Verifica se o status HTTP é 201 (Created)
                    .body("nome", is("Roberta Campos"));   // Verifica se o nome retornado na resposta é correto
        }

        @Test
        void naoDeveCadastrarClienteComDadosInvalidos() {
            given()
                    .contentType(ContentType.JSON)
                    .body(new ClienteDtoRequest("", "", "", "", "", "", "", "", "", "", ""))
            .when()
                    .post("/clientes")
            .then()
                    .statusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }

    @Nested
    class AtualizarClientes {
        @Test
        void deveAtualizarCliente() {
            int codigoCliente = 2; // Este deve ser o ID de Maria Oliveira que você quer atualizar
            ClienteDtoRequest clienteAtualizado = new ClienteDtoRequest(
                    "Maria Oliveira", // Nome que pode ser atualizado
                    "987.654.321-98", // Mantém o CPF original
                    "maria.updated@email.com", // Novo email para teste
                    "98765-432", "Avenida Brasil Atualizada", "200", "Casa 2", "Jardim Novo",
                    "Rio de Janeiro", "RJ", "(21) 99876-5432" // Outros campos atualizados
            );

            given()
                    .contentType(ContentType.JSON)
                    .pathParam("codigoCliente", codigoCliente)
                    .body(clienteAtualizado)
            .when()
                    .put("/clientes/{codigoCliente}")
            .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("nome", is("Maria Oliveira"));  // Confirma que o nome foi atualizado
        }

        @Test
        void naoDeveAtualizarClienteInexistente() {
            given()
                    .contentType(ContentType.JSON)
                    .pathParam("codigoCliente", 9999)
                    .body(new ClienteDtoRequest("Inexistente", "000.000.000-00", "inexistente@email.com", "00000-000", "Rua Inexistente", "0", "Apto 0", "Bairro Inexistente", "Cidade Inexistente", "XX", "(00) 00000-0000"))
            .when()
                    .put("/clientes/{codigoCliente}")
            .then()
                    .statusCode(HttpStatus.SC_NOT_FOUND);
        }

        @Test
        void deveAtualizarClientePorEmailComSucesso() {
            String emailOriginal = "maria.oliveira@email.com";
            ClienteDtoRequest clienteDto = new ClienteDtoRequest(
                    "Maria Oliveira Atualizada", "987.654.321-98", "maria.new@email.com",
                    "98765-432", "Avenida Brasil Atualizada", "200", "Casa 2", "Jardim Novo",
                    "Rio de Janeiro", "RJ", "(21) 99876-5432"
            );

            given()
                    .contentType(ContentType.JSON)
                    .pathParam("email", emailOriginal)
                    .body(clienteDto)
            .when()
                    .put("/clientes/email/{email}")
            .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("email", is(clienteDto.email()));
        }

        @Test
        void naoDeveAtualizarClientePorEmailSeEmailJaExiste() {
            String emailOriginal = "maria.oliveira@email.com";
            ClienteDtoRequest clienteDto = new ClienteDtoRequest(
                    "Maria Oliveira Atualizada", "987.654.321-98", "carlos.pereira@email.com",
                    "98765-432", "Avenida Brasil Atualizada", "200", "Casa 2", "Jardim Novo",
                    "Rio de Janeiro", "RJ", "(21) 99876-5432"
            );

            given()
                    .contentType(ContentType.JSON)
                    .pathParam("email", emailOriginal)
                    .body(clienteDto)
            .when()
                    .put("/clientes/email/{email}")
            .then()
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body("message", containsString("Email já cadastrado"));
        }
    }

    @Nested
    class ExcluirClientes {
        @Test
        void naoDeveExcluirClienteCasoServicoPedidosEstejaFora() {
            given()
                    .pathParam("codigoCliente", 1)
            .when()
                    .delete("/clientes/{codigoCliente}")
            .then()
                    .statusCode(HttpStatus.SC_NOT_FOUND);
        }

        @Test
        void naoDeveExcluirClienteInexistente() {
            given()
                    .pathParam("codigoCliente", 50000)
            .when()
                    .delete("/clientes/{codigoCliente}")
            .then()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("message", is("Cliente com código 50000 não encontrado"));
        }

        @Test
        void naoDeveExcluirClientePorEmailCasoServicoPedidoEstejaFora() {
            String email = "ana.costa@email.com";
            given()
                    .pathParam("email", email)
            .when()
                    .delete("/clientes/email/{email}")
            .then()
                    .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body("message", is("Connection refused: no further information executing POST http://localhost:8084/cliente/possui-pedidos?codigoCliente=4"))
                    ;
        }

        @Test
        void naoDeveExcluirClientePorEmailInexistente() {
            String emailInexistente = "inexistente@example.com"; // E-mail que não existe na base de dados
            given()
                    .pathParam("email", emailInexistente)
            .when()
                    .delete("/clientes/email/{email}")
            .then()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("message", is("Cliente com Email " + emailInexistente + " não encontrado"));
        }
    }
}

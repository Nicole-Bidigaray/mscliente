package com.fiap.techchallenger4.mscliente.infra.swagger.annotations.responses;

import com.fiap.techchallenger4.mscliente.infra.handler.MessageErrorHandler;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })

@ApiResponse(
	responseCode = "404", 
	description = "Registro solicitado não encontrado.",
    content = { 
		@Content(
			mediaType = MediaType.APPLICATION_JSON_VALUE,
			schema = @Schema(implementation = MessageErrorHandler.class)
		)
})
public @interface ApiResponseNotFoundJson {}

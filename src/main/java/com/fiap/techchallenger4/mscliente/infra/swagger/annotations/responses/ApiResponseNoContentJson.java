package com.fiap.techchallenger4.mscliente.infra.swagger.annotations.responses;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })

@ApiResponse(
	responseCode = "204", 
	description = "Operação realizada com sucesso, mas sem arquivo de retorno",
	content = @Content()
)
public @interface ApiResponseNoContentJson{}

package com.fiap.techchallenger4.mscliente.infra.swagger.annotations;

import com.fiap.techchallenger4.mscliente.infra.swagger.annotations.responses.ApiResponseBadRequestJson;
import com.fiap.techchallenger4.mscliente.infra.swagger.annotations.responses.ApiResponseCreateJson;
import com.fiap.techchallenger4.mscliente.infra.swagger.annotations.responses.ApiResponseNotFoundJson;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ METHOD })
@ApiResponseCreateJson
@ApiResponseNotFoundJson
@ApiResponseBadRequestJson
public @interface ApiResponseSwaggerCreate {}

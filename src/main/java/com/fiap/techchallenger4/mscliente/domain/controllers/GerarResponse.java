package com.fiap.techchallenger4.mscliente.domain.controllers;

import com.fiap.techchallenger4.mscliente.domain.exceptions.BusinessException;

@FunctionalInterface
public interface GerarResponse<T> {

    T get() throws BusinessException;
}

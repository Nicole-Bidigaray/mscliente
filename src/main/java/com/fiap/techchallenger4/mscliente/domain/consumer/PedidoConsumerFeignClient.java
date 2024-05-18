package com.fiap.techchallenger4.mscliente.domain.consumer;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="PedidoConsumerFeignClient",url="${url.pedido.consumer.feign.client}")
public interface PedidoConsumerFeignClient {
    
    @PostMapping("/cliente/possui-pedidos")
    Map<String, Boolean> clientePossuiPedidos(@RequestParam("codigoCliente") final Long codigoCliente);

}

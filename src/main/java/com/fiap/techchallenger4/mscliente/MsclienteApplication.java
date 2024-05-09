package com.fiap.techchallenger4.mscliente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsclienteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsclienteApplication.class, args);
	}

}

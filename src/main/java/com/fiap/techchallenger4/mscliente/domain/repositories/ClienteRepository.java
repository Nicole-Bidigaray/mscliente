package com.fiap.techchallenger4.mscliente.domain.repositories;

import com.fiap.techchallenger4.mscliente.domain.entities.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    ClienteEntity findByCpf(String cpf);
}

package com.fiap.techchallenger4.mscliente.domain.repositories;

import com.fiap.techchallenger4.mscliente.domain.entities.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    ClienteEntity findByCodigoCliente(Long codigoCliente);
    ClienteEntity findByCpf(String cpf);
}

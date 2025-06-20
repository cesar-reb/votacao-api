package com.cesar.votacao.api.port.out;

import com.cesar.votacao.api.domain.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Long> {
}

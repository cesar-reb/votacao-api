package com.cesar.votacao.api.port.out;

import com.cesar.votacao.api.domain.model.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PautaRepository extends JpaRepository<Pauta, Long> {
}

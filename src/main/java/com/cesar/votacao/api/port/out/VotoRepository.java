package com.cesar.votacao.api.port.out;

import com.cesar.votacao.api.adapter.out.persistence.ContagemVotoProjecao;
import com.cesar.votacao.api.domain.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsBySessaoIdAndCpfAssociado(Long id, String cpfAssociado);

    @Query("""
                SELECT v.opcao AS opcao, COUNT(v) AS total
                FROM Voto v
                WHERE v.sessao.id = :sessaoId
                GROUP BY v.opcao
            """)
    List<ContagemVotoProjecao> contarVotosPorSessao(@Param("sessaoId") Long sessaoId);
}

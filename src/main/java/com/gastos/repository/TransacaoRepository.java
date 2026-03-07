package com.gastos.repository;

import com.gastos.model.Transacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TransacaoRepository implements PanacheRepository<Transacao> {

    public List<Transacao> findByCategoria(String categoria) {
        return list("categoria", categoria);
    }

    public List<Transacao> findRecentes(int limite) {
        return find("ORDER BY data DESC")
                .page(0, limite)
                .list();
    }
}

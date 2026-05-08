package com.gastos.repository;

import com.gastos.model.Transaction;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TransactionRepository implements PanacheRepository<Transaction> {

    public List<Transaction> findByCategory(String category) {
        return list("category", category);
    }

    public List<Transaction> findRecent(int limit) {
        return find("ORDER BY date DESC")
                .page(0, limit)
                .list();
    }
}
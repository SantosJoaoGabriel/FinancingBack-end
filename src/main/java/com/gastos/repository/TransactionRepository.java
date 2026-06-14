package com.gastos.repository;

import com.gastos.model.Transaction;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class TransactionRepository implements PanacheRepository<Transaction> {

    public List<Transaction> findByUserId(Long userId) {
        return list("user.id", userId);
    }

    public List<Transaction> findByUserIdAndCategory(Long userId, String category) {
        return list("user.id = ?1 and category = ?2", userId, category);
    }

    public List<Transaction> findRecentByUserId(Long userId, int limit) {
        return find("user.id = ?1 ORDER BY date DESC", userId)
                .page(0, limit)
                .list();
    }

    public List<Transaction> findByUserIdAndPeriod(Long userId, LocalDate startDate, LocalDate endDate) {
        return list("user.id = ?1 and date >= ?2 and date <= ?3 order by date desc",
                userId, startDate, endDate);
    }
}
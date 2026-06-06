package com.gastos.repository;

import com.gastos.model.ReportHistory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ReportHistoryRepository implements PanacheRepository<ReportHistory> {

    public List<ReportHistory> findByUserId(Long userId) {
        return find("userId", Sort.by("createdAt").descending(), userId).list();
    }
}
package com.gastos.service;

import com.gastos.dto.TransactionDTO;
import com.gastos.model.Transaction;
import com.gastos.repository.TransactionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TransactionService {

    @Inject
    TransactionRepository repository;

    public List<TransactionDTO> findAll() {
        return repository.listAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findRecent(int limit) {
        return repository.findRecent(limit)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO findById(Long id) {
        Transaction transaction = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        return toDTO(transaction);
    }

    @Transactional
    public TransactionDTO create(TransactionDTO dto) {
        Transaction transaction = toEntity(dto);
        transaction.id = null;
        repository.persist(transaction);
        return toDTO(transaction);
    }

    @Transactional
    public TransactionDTO update(Long id, TransactionDTO dto) {
        Transaction existingTransaction = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        existingTransaction.description = dto.description;
        existingTransaction.category = dto.category;
        existingTransaction.date = dto.date;
        existingTransaction.amount = dto.amount;
        existingTransaction.type = dto.type;
        existingTransaction.paymentMethod = dto.paymentMethod;
        existingTransaction.notes = dto.notes;

        return toDTO(existingTransaction);
    }

    @Transactional
    public void delete(Long id) {
        Transaction transaction = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        repository.delete(transaction);
    }

    private TransactionDTO toDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.id = transaction.id;
        dto.description = transaction.description;
        dto.category = transaction.category;
        dto.date = transaction.date;
        dto.amount = transaction.amount;
        dto.type = transaction.type;
        dto.paymentMethod = transaction.paymentMethod;
        dto.notes = transaction.notes;
        return dto;
    }

    private Transaction toEntity(TransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.description = dto.description;
        transaction.category = dto.category;
        transaction.date = dto.date;
        transaction.amount = dto.amount;
        transaction.type = dto.type;
        transaction.paymentMethod = dto.paymentMethod;
        transaction.notes = dto.notes;
        return transaction;
    }
}
package com.gastos.service;

import com.gastos.dto.TransactionDTO;
import com.gastos.model.Transaction;
import com.gastos.model.User;
import com.gastos.repository.TransactionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TransactionService {

    @Inject
    TransactionRepository repository;

    public List<TransactionDTO> findAll(String userEmail) {
        User user = findUserByEmail(userEmail);

        return repository.findByUserId(user.id)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findRecent(String userEmail, int limit) {
        User user = findUserByEmail(userEmail);

        return repository.findRecentByUserId(user.id, limit)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO findById(String userEmail, Long id) {
        User user = findUserByEmail(userEmail);

        Transaction transaction = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        if (!transaction.user.id.equals(user.id)) {
            throw new ForbiddenException("Acesso negado a esta transação");
        }

        return toDTO(transaction);
    }

    @Transactional
    public TransactionDTO create(String userEmail, TransactionDTO dto) {
        User user = findUserByEmail(userEmail);

        Transaction transaction = toEntity(dto);
        transaction.id = null;
        transaction.user = user;

        repository.persist(transaction);
        return toDTO(transaction);
    }

    @Transactional
    public TransactionDTO update(String userEmail, Long id, TransactionDTO dto) {
        User user = findUserByEmail(userEmail);

        Transaction existingTransaction = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        if (!existingTransaction.user.id.equals(user.id)) {
            throw new ForbiddenException("Acesso negado a esta transação");
        }

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
    public void delete(String userEmail, Long id) {
        User user = findUserByEmail(userEmail);

        Transaction transaction = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        if (!transaction.user.id.equals(user.id)) {
            throw new ForbiddenException("Acesso negado a esta transação");
        }

        repository.delete(transaction);
    }

    private User findUserByEmail(String email) {
        User user = User.find("email", email).firstResult();

        if (user == null) {
            throw new NotFoundException("Usuário autenticado não encontrado");
        }

        return user;
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
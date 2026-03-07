package com.gastos.service;

import com.gastos.model.Transacao;
import com.gastos.repository.TransacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class TransacaoService {

    @Inject
    TransacaoRepository repository;

    public List<Transacao> listarTodas() {
        return repository.listAll();
    }

    public List<Transacao> listarRecentes(int limite) {
        return repository.findRecentes(limite);
    }

    public Transacao buscarPorId(Long id) {
        return repository.findByIdOptional(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
    }

    @Transactional
    public Transacao criar(Transacao transacao) {
        repository.persist(transacao);
        return transacao;
    }

    @Transactional
    public Transacao atualizar(Long id, Transacao dados) {
        Transacao existente = buscarPorId(id);
        existente.descricao = dados.descricao;
        existente.categoria = dados.categoria;
        existente.data = dados.data;
        existente.valor = dados.valor;
        existente.paymentMethod = dados.paymentMethod;
        existente.notes = dados.notes;
        return existente;
    }

    @Transactional
    public void deletar(Long id) {
        Transacao transacao = buscarPorId(id);
        repository.delete(transacao);
    }
}

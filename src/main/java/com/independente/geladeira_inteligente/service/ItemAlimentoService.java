package com.independente.geladeira_inteligente.service;

import com.independente.geladeira_inteligente.model.ItemAlimentoModel;
import com.independente.geladeira_inteligente.repository.ItemAlimentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemAlimentoService {

    private final ItemAlimentoRepository itemAlimentoRepository;

    public ItemAlimentoService(ItemAlimentoRepository itemAlimentoRepository) {
        this.itemAlimentoRepository = itemAlimentoRepository;
    }

    public ItemAlimentoModel adicionarFood(ItemAlimentoModel foodNovo) {
        return itemAlimentoRepository.save(foodNovo);
    }

    public List<ItemAlimentoModel> listarTodos() {
        return itemAlimentoRepository.findAll();
    }

    public Optional<ItemAlimentoModel> buscarPorId(Long id) {
        return itemAlimentoRepository.findById(id);
    }

    public ItemAlimentoModel atualizar(ItemAlimentoModel foodItemAtualizado) {
        return itemAlimentoRepository.save(foodItemAtualizado);
    }

    public void excluir(Long id) {
        itemAlimentoRepository.deleteById(id);
    }

}

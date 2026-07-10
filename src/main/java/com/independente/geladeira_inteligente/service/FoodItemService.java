package com.independente.geladeira_inteligente.service;

import com.independente.geladeira_inteligente.model.FoodItemModel;
import com.independente.geladeira_inteligente.repository.FoodItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    public FoodItemModel adicionarFood(FoodItemModel foodNovo) {
        return foodItemRepository.save(foodNovo);
    }

    public List<FoodItemModel> listarTodos() {
        return foodItemRepository.findAll();
    }

    public Optional<FoodItemModel> buscarPorId(Long id) {
        return foodItemRepository.findById(id);
    }

    public FoodItemModel atualizar(FoodItemModel foodItemAtualizado) {
        return foodItemRepository.save(foodItemAtualizado);
    }

    public void excluir(Long id) {
        foodItemRepository.deleteById(id);
    }

}

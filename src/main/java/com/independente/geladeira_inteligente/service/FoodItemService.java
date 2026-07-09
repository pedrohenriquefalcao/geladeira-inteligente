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

    public FoodItemModel adicionarFood(FoodItemModel foodItemModel) {
        FoodItemModel foodNovo = new FoodItemModel();
        return foodItemRepository.save(foodNovo);
    }

    public List<FoodItemModel> listarTodos() {
        return foodItemRepository.findAll();
    }

    public FoodItemModel listarPorId(Long id) {
        return foodItemRepository.findById(id).orElse(null);
    }

    public FoodItemModel alterarPorId(Long id, FoodItemModel foodItemAtualizado) {
        Optional<FoodItemModel> foodItemModelOptional = foodItemRepository.findById(id);
        if (foodItemModelOptional.isPresent()) {
            foodItemAtualizado.setId(id);
            return foodItemRepository.save(foodItemAtualizado);
        }
        return null;
    }

    public void deletarPorId(Long id) {
        foodItemRepository.deleteById(id);
    }

}

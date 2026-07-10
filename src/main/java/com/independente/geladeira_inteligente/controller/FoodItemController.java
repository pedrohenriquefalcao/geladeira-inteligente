package com.independente.geladeira_inteligente.controller;

import com.independente.geladeira_inteligente.model.FoodItemModel;
import com.independente.geladeira_inteligente.service.FoodItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
public class FoodItemController {

    private FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }


    @PostMapping("/salvar")
    public ResponseEntity<FoodItemModel> adicionarNovo(@RequestBody FoodItemModel foodItemModel) {
        FoodItemModel salvo = foodItemService.adicionarFood(foodItemModel);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("/listar/todos")
    public ResponseEntity<List<FoodItemModel>> listarTodos() {
        List<FoodItemModel> relacaoGeral = foodItemService.listarTodos();
        return ResponseEntity.ok(relacaoGeral);
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<FoodItemModel> buscarPorId(@PathVariable Long id) {
        return foodItemService.buscarPorId(id)
                .map(food -> ResponseEntity.ok(food))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/alterar/{id}")
    public ResponseEntity<FoodItemModel> atualizar(@RequestBody FoodItemModel foodItemModel, @PathVariable Long id) {
        return foodItemService.buscarPorId(id)
                .map(itemExistente -> {
                    foodItemModel.setId(itemExistente.getId());
                    FoodItemModel atualizado = foodItemService.atualizar(foodItemModel);
                    return ResponseEntity.ok(atualizado);
                }).orElse(ResponseEntity.notFound().build());

    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        foodItemService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}

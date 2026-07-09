package com.independente.geladeira_inteligente.controller;

import com.independente.geladeira_inteligente.model.FoodItemModel;
import com.independente.geladeira_inteligente.service.FoodItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/food")
public class FoodItemController {

    private FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    public ResponseEntity<FoodItemModel> adicionarNovo(@RequestBody FoodItemModel foodItemModel) {
        FoodItemModel salvo = foodItemService.adicionarFood(foodItemModel);
        return ResponseEntity.ok(salvo);
    }

    public ResponseEntity<List<FoodItemModel>> listarTodos() {
        List<FoodItemModel> relacaoGeral = foodItemService.listarTodos();
        return ResponseEntity.ok(relacaoGeral);
    }

    public ResponseEntity<?> listarPorId(@PathVariable Long id) {
        FoodItemModel foodBuscado = foodItemService.listarPorId(id);
        if (foodBuscado != null) {
            return ResponseEntity.ok(foodBuscado);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Alimento de ID " + id + " não foi encontrado no sistema!");
    }

    public ResponseEntity<?> deletarPorId(@PathVariable Long id) {
        FoodItemModel foodBuscado = foodItemService.listarPorId(id);
        if (foodBuscado != null) {
            foodItemService.deletarPorId(id);
            return ResponseEntity.ok("Alimento de ID " + id + " deletado!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Alimento de ID " + id + " não foi encontrado no sistema!");
    }

}

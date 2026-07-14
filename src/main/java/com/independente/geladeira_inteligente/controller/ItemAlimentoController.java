package com.independente.geladeira_inteligente.controller;

import com.independente.geladeira_inteligente.model.ItemAlimentoModel;
import com.independente.geladeira_inteligente.service.ItemAlimentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
public class ItemAlimentoController {

    private ItemAlimentoService itemAlimentoService;

    public ItemAlimentoController(ItemAlimentoService itemAlimentoService) {
        this.itemAlimentoService = itemAlimentoService;
    }


    @PostMapping("/salvar")
    public ResponseEntity<ItemAlimentoModel> adicionarNovo(@RequestBody ItemAlimentoModel itemAlimentoModel) {
        ItemAlimentoModel salvo = itemAlimentoService.adicionarFood(itemAlimentoModel);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("/listar/todos")
    public ResponseEntity<List<ItemAlimentoModel>> listarTodos() {
        List<ItemAlimentoModel> relacaoGeral = itemAlimentoService.listarTodos();
        return ResponseEntity.ok(relacaoGeral);
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<ItemAlimentoModel> buscarPorId(@PathVariable Long id) {
        return itemAlimentoService.buscarPorId(id)
                .map(food -> ResponseEntity.ok(food))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/alterar/{id}")
    public ResponseEntity<ItemAlimentoModel> atualizar(@RequestBody ItemAlimentoModel itemAlimentoModel, @PathVariable Long id) {
        return itemAlimentoService.buscarPorId(id)
                .map(itemExistente -> {
                    itemAlimentoModel.setId(itemExistente.getId());
                    ItemAlimentoModel atualizado = itemAlimentoService.atualizar(itemAlimentoModel);
                    return ResponseEntity.ok(atualizado);
                }).orElse(ResponseEntity.notFound().build());

    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        itemAlimentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}

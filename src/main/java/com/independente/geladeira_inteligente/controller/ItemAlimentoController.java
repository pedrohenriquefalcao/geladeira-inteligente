package com.independente.geladeira_inteligente.controller;

import com.independente.geladeira_inteligente.model.ItemAlimentoModel;
import com.independente.geladeira_inteligente.exception.ApiValidationException;
import com.independente.geladeira_inteligente.exception.ApiNotFoundException;
import com.independente.geladeira_inteligente.service.ItemAlimentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/alimento")
public class ItemAlimentoController {

    private static final Logger log = LoggerFactory.getLogger(ItemAlimentoController.class);
    private final ItemAlimentoService itemAlimentoService;

    public ItemAlimentoController(ItemAlimentoService itemAlimentoService) {
        this.itemAlimentoService = itemAlimentoService;
    }


    @PostMapping("/salvar")
    public ResponseEntity<ItemAlimentoModel> adicionarNovo(@RequestBody ItemAlimentoModel itemAlimentoModel) {
        validar(itemAlimentoModel);
        log.info("Adicionando alimento: nome='{}', categoria={}, quantidade={}",
                itemAlimentoModel.getNome(), itemAlimentoModel.getCategoria(), itemAlimentoModel.getQuantidade());
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
                .orElseThrow(() -> new ApiNotFoundException("Alimento não encontrado para o id " + id + "."));
    }

    @PutMapping("/alterar/{id}")
    public ResponseEntity<ItemAlimentoModel> atualizar(@RequestBody ItemAlimentoModel itemAlimentoModel, @PathVariable Long id) {
        validar(itemAlimentoModel);
        return itemAlimentoService.buscarPorId(id)
                .map(itemExistente -> {
                    itemAlimentoModel.setId(itemExistente.getId());
                    log.info("Atualizando alimento id={}: nome='{}'", id, itemAlimentoModel.getNome());
                    ItemAlimentoModel atualizado = itemAlimentoService.atualizar(itemAlimentoModel);
                    return ResponseEntity.ok(atualizado);
                }).orElseThrow(() -> new ApiNotFoundException("Alimento não encontrado para o id " + id + "."));

    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (itemAlimentoService.buscarPorId(id).isEmpty()) {
            log.warn("Tentativa de excluir alimento inexistente: id={}", id);
            throw new ApiNotFoundException("Alimento não encontrado para o id " + id + ".");
        }
        log.info("Excluindo alimento id={}", id);
        itemAlimentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private void validar(ItemAlimentoModel alimento) {
        if (alimento == null) {
            throw new ApiValidationException("O alimento não foi informado.");
        }
        if (alimento.getNome() == null || alimento.getNome().trim().length() < 2) {
            throw new ApiValidationException("O nome deve ter pelo menos 2 caracteres.");
        }
        if (alimento.getCategoria() == null) {
            throw new ApiValidationException("A categoria é obrigatória.");
        }
        if (alimento.getQuantidade() == null || alimento.getQuantidade() < 1 || alimento.getQuantidade() > 999) {
            throw new ApiValidationException("A quantidade deve estar entre 1 e 999.");
        }
        if (alimento.getValidade() == null || !alimento.getValidade().isAfter(LocalDate.now())) {
            throw new ApiValidationException("A validade deve ser posterior à data atual.");
        }
    }
}

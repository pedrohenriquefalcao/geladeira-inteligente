package com.independente.geladeira_inteligente.controller;

import com.independente.geladeira_inteligente.model.ItemAlimentoModel;
import com.independente.geladeira_inteligente.repository.ItemAlimentoRepository;
import com.independente.geladeira_inteligente.service.ChatGptService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/receita")
public class ReceitaController {

    private final ChatGptService chatGptService;
    private final ItemAlimentoRepository alimentoRepository;

    public ReceitaController(ChatGptService chatGptService, ItemAlimentoRepository alimentoRepository) {
        this.chatGptService = chatGptService;
        this.alimentoRepository = alimentoRepository;
    }

    @PostMapping("/gerar")
    public Mono<String> obterReceita() {

        List<ItemAlimentoModel> alimentos = alimentoRepository.findAll();

        if (alimentos.isEmpty()) {
            return Mono.just("Sua geladeira está vazia! Adicione ingredientes antes de pedir uma receita.");
        }


        String ingredientesDaGeladeira = alimentos.stream()
                .map(ItemAlimentoModel::getNome)
                .collect(Collectors.joining(", "));

        return chatGptService.obterSugestaoReceita(ingredientesDaGeladeira);
    }
}
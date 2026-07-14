package com.independente.geladeira_inteligente.controller;

import com.independente.geladeira_inteligente.service.ChatGptService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/receita")
public class ReceitaController {

    private final ChatGptService chatGptService;

    public ReceitaController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }


    @PostMapping("/gerar")
    public Mono<String> obterReceita(@RequestBody Map<String, String> body) {
        String prompt = body.get("prompt");
        return chatGptService.obterSugestaoReceita(prompt);
    }
}
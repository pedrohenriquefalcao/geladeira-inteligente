package com.independente.geladeira_inteligente.service;

import com.independente.geladeira_inteligente.dto.GptDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ChatGptService {

    private final WebClient webClient;

    @Value("${API_KEY}")
    private String apiKey;

    public ChatGptService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> obterSugestaoReceita(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            return Mono.error(new IllegalStateException("API_KEY não configurada"));
        }

        GptDTO payload = GptDTO.builder()
                .model("gpt-4o-mini")
                .prompt(prompt)
                .build();

        return this.webClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    return Mono.just("Erro ao comunicar com o GPT: " + e.getMessage());
                });
    }
}
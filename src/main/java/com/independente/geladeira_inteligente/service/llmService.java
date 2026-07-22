package com.independente.geladeira_inteligente.service;

import com.independente.geladeira_inteligente.dto.GptDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;


import java.util.List;

@Service
public class llmService {

    private final WebClient webClient;

    @Value("${API_KEY}")
    private String apiKey;

    public llmService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> obterSugestaoReceita(String ingredientes) {
        if (apiKey == null || apiKey.isEmpty()) {
            return Mono.error(new IllegalStateException("API KEY não configurada"));
        }

        String prompt = """
                Você é um Chef de Cozinha focado em evitar o desperdício de alimentos.
                                Crie uma receita criativa e saborosa utilizando prioritariamente os seguintes ingredientes: %s.
                
                                Siga as seguintes regras:
                                1. Quero que você justifique sempre o por que de ter escolhido a receita (exemplo: como você tem {ingrediente} e {ingrediente}, podemos fazer esta receita).
                                2. Liste todos os ingredientes que você encontrou na relação de dados. Nome e quantidade.
                                3. Dê um nome chamativo para a receita.
                                4. Liste o tempo de preparo estimado.
                                5. Escreva o passo a passo de forma simples e direta.
                                6. Se algum ingrediente for muito incompatível, sinta-se livre para sugerir um substituto simples.
                """.formatted(ingredientes);

        GptDTO payload = GptDTO.builder()
                .model("llama-3.1-8b-instant")
                .messages(List.of(new GptDTO.Message("user", prompt)))
                .build();

        return this.webClient.post()
                .uri("chat/completions")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> jsonNode
                        .path("choices")
                        .path(0)
                        .path("message")
                        .path("content")
                        .asText()
                )
                .onErrorResume(WebClientResponseException.class, e -> {
                    String corpoDoErro = e.getResponseBodyAsString();
                    System.err.println("ERRO DETALHADO DA GROQ: " + corpoDoErro);
                    return Mono.just("Erro da API: " + corpoDoErro);
                })
                .onErrorResume(Exception.class, e -> {
                    return Mono.just("Erro genérico: " + e.getMessage());
                });
    }
}
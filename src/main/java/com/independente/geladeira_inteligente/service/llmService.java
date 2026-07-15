package com.independente.geladeira_inteligente.service;

import com.independente.geladeira_inteligente.dto.GptDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

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
                                1. Dê um nome chamativo para a receita.
                                2. Liste o tempo de preparo estimado.
                                3. Escreva o passo a passo de forma simples e direta.
                                4. Se algum ingrediente for muito incompatível, sinta-se livre para sugerir um substituto simples.
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
                .bodyToMono(String.class)
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
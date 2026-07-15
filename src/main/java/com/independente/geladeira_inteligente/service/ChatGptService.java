package com.independente.geladeira_inteligente.service;

import com.independente.geladeira_inteligente.dto.GptDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ChatGptService {

    private final WebClient webClient;

    @Value("${API_KEY}")
    private String apiKey;

    public ChatGptService(WebClient webClient) {
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
                .model("llama3-8b-8192")
                .messages(List.of(new GptDTO.Message("user", prompt)))
                .build();

        return this.webClient.post()
                .uri("chat/completions")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    return Mono.just("Erro ao comunicar com a API de IA: " + e.getMessage());
                });
    }
}
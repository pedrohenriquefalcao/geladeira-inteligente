package com.independente.geladeira_inteligente.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.independente.geladeira_inteligente.dto.GptDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class llmService {

    private static final Logger log = LoggerFactory.getLogger(llmService.class);
    private final WebClient webClient;

    @Value("${GROQ_API_KEY:${API_KEY:}}")
    private String apiKey;

    @Value("${llm.api.model:llama-3.1-8b-instant}")
    private String primaryModel;

    @Value("#{'${llm.api.fallback-models:mixtral-8x7b-32768}'.split(',')}")
    private List<String> fallbackModels;

    public llmService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> obterSugestaoReceita(String ingredientes) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.error("Geração de receita recusada: GROQ_API_KEY/API_KEY não configurada.");
            return Mono.error(new IllegalStateException("API KEY não configurada"));
        }

        String prompt = """
                Você é um Chef de Cozinha focado em evitar o desperdício de alimentos.
                Crie uma receita criativa e saborosa utilizando prioritariamente os seguintes ingredientes: %s.
                
                Siga as seguintes regras:
                1. Quero que você justifique sempre o por que de ter escolhido a receita.
                2. Liste todos os ingredientes que você encontrou na relação de dados. Nome e quantidade.
                3. Dê um nome chamativo para a receita.
                4. Liste o tempo de preparo estimado.
                5. Escreva o passo a passo de forma simples e direta.
                6. Se algum ingrediente for muito incompatível, sinta-se livre para sugerir um substituto simples.
                7. A mensagem passada deve seguir a seguinte regra:
                [Título da receita]
                [Ingredientes que serão utilizados e quanto vai ser utilizado]
                [Tempo de preparo estimado]
                [Passo a passo]
                [Observação, se tiver]
                8. Só deve fornecer uma receita se todos os ingredientes necessários para a receita estiverem disponíveis no estoque.
                9. Não use asterísticos para deixar em negrito.
                """.formatted(ingredientes);

        return enviarRequisicao(prompt, primaryModel)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getResponseBodyAsString().contains("model_not_found")) {
                        log.warn("Modelo {} indisponível. Tentando modelo reserva.", primaryModel);
                        return tentarFallbacks(prompt, 0);
                    }
                    log.error("Groq recusou a geração da receita: status={}, resposta={}",
                            e.getStatusCode(), e.getResponseBodyAsString());
                    return Mono.just("Não foi possível gerar a receita agora. A IA recusou a solicitação; tente novamente em instantes.");
                });
    }

    private Mono<String> tentarFallbacks(String prompt, int index) {
        if (index >= fallbackModels.size()) {
            return Mono.just("Erro: Nenhum modelo configurado está disponível no momento na Groq.");
        }

        String nextModel = fallbackModels.get(index).trim();
        log.info("Tentando modelo fallback: {}", nextModel);

        return enviarRequisicao(prompt, nextModel)
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.warn("Modelo fallback {} falhou: status={}", nextModel, e.getStatusCode());
                    return tentarFallbacks(prompt, index + 1);
                });
    }

    private Mono<String> enviarRequisicao(String prompt, String model) {
        GptDTO payload = GptDTO.builder()
                .model(model)
                .messages(List.of(new GptDTO.Message("user", prompt)))
                .build();

        return this.webClient.post()
                .uri("/chat/completions")
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
                );
    }
}
package com.pokeservice.InfoApi;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.Duration;


@Slf4j
@Service
public class PokemonService {
    private final WebClient webClient;

    @Value("${pokemon.api.url}")
    private String pokemonApiUrl;
    @Value("${pokemon.api.server}")
    private String pokemonApiServer;

    @Autowired
    public PokemonService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(pokemonApiUrl).build();
    }
    @Cacheable(value = "pokemonCache", key = "#offset + '-' + #limit")
    @CircuitBreaker(name = "pokemonService", fallbackMethod = "fallbackGetPokemonList")
    public String getPokemonList(int offset, int limit) {
        String parameters = String.format("?offset=%d&limit=%d", offset, limit);
        log.info("Iniciando chamada à API de Pokémon com parâmetros: {}", parameters);

        try {
            String response = webClient.get()
                    .uri(UriComponentsBuilder.fromHttpUrl(pokemonApiUrl).query(parameters).build().toUri())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        log.error("Erro ao chamar a API de Pokémon: {} com status {}",
                                pokemonApiUrl.concat(parameters), clientResponse.statusCode());
                        return Mono.error(new PokemonApiException("Erro ao acessar a Pokémon API"));
                    })
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))  // Timeout de 5 segundos
                    .block();

            if (response != null) {
                // Substitui a URL da PokéAPI pela URL do servidor local
                response = response.replaceAll(pokemonApiServer, getServerUrl());
            }
            return response;

        } catch (Exception ex) {
            log.error("Erro inesperado ao acessar a Pokémon API: {}", ex.getMessage());
            throw new PokemonApiException("Falha ao recuperar a lista de Pokémon", ex);
        }
    }
    //inicio get pokemon por id
    @Cacheable(value = "pokemonByIdCache", key = "#id")
    public String getPokemon(int id) {
        log.info("Executando método getPokemon para {}", id);
        try {
            String response = webClient.get()
                    .uri(UriComponentsBuilder.fromHttpUrl(pokemonApiUrl).pathSegment(String.valueOf(id)).build().toUri())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        log.error("Erro ao chamar a API de Pokémon: {} com status {}",
                                pokemonApiUrl.concat("/"+id), clientResponse.statusCode());
                        return Mono.error(new PokemonApiException("Erro ao acessar a Pokémon API"));
                    })
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))  // Timeout de 5 segundos
                    .block();

            if (response != null) {
                // Substitui a URL da PokéAPI pela URL do servidor local
                response = response.replaceAll(pokemonApiServer, getServerUrl());
            }
            return response;

        } catch (Exception ex) {
            log.error("Erro inesperado ao acessar a Pokémon API: {}", ex.getMessage());
            throw new PokemonApiException("Falha ao recuperar a lista de Pokémon", ex);
        }
    }
    //fim get pokemon por id
    // Método de fallback para quando o CircuitBreaker for acionado
    public PokemonsRepresentationModel fallbackGetPokemonList(int offset, int limit, Throwable throwable) {
        log.warn("Circuit breaker ativado. Usando fallback para a chamada de lista de Pokémon: {}", throwable.getMessage());
        // Pode retornar um objeto de resposta vazio ou algum dado default
        return buildPokemonsRepresentationModel("Fallback response", offset, limit);
    }

    // Método auxiliar para construir o modelo de resposta com links HATEOAS
    private PokemonsRepresentationModel buildPokemonsRepresentationModel(String response, int offset, int limit) {
        return PokemonsRepresentationModel.builder()
                .pokemonData(response)
                .build()
                .add(WebMvcLinkBuilder.linkTo(methodOn(PokemonController.class).getPokemons(Math.max(offset - limit, 0), limit))
                        .withRel("previous"))
                .add(WebMvcLinkBuilder.linkTo(methodOn(PokemonController.class).getPokemons(offset + limit, limit))
                        .withRel("next"));
    }
    private String getServerUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }
}

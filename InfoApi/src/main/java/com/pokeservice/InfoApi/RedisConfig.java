package com.pokeservice.InfoApi;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Configuração de cache para Pokémon por ID
        RedisCacheConfiguration pokemonByIdCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))  // Exemplo de TTL para Pokémon por ID
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        // Configuração de cache para lista de Pokémon
        RedisCacheConfiguration pokemonListCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))  // Exemplo de TTL para a lista de Pokémon
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        // Criando o CacheManager com múltiplas zonas de cache
        return RedisCacheManager.builder(redisConnectionFactory)
                .withCacheConfiguration("pokemonCache", pokemonListCacheConfig)  // Para a lista de Pokémon
                .withCacheConfiguration("pokemonByIdCache", pokemonByIdCacheConfig)  // Para Pokémon por ID
                .build();
    }
}


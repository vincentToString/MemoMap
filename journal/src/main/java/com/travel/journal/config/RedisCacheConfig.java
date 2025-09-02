package com.travel.journal.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class RedisCacheConfig {
    @Bean
    public GenericJackson2JsonRedisSerializer redisJsonSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.WRAPPER_ARRAY
        );
        return new GenericJackson2JsonRedisSerializer(mapper);
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(@Value("${spring.cache.redis.time-to-live}") Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                                      .entryTtl(ttl)
                                      .serializeValuesWith(
                                              RedisSerializationContext.SerializationPair.fromSerializer(redisJsonSerializer())
                                      );

    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory,
                                               RedisCacheConfiguration redisCacheConfiguration) {
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(redisCacheConfiguration).build();
    }
}

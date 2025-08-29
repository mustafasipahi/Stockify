package com.stockify.project.configuration;

import com.stockify.project.configuration.properties.CacheProperties;
import com.stockify.project.configuration.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.stockify.project.constant.CacheConstants.*;

@EnableCaching
@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisProperties redisProperties;
    private final CacheProperties cacheProperties;

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedissonConnectionFactory redisConnectionFactory() {
        return new RedissonConnectionFactory(constructRedissonClient());
    }

    @Bean
    public CacheManager cacheManager() {
        return RedisCacheManager.builder(redisConnectionFactory())
                .withInitialCacheConfigurations(constructInitialCacheConfigurations())
                .transactionAware()
                .build();
    }

    private RedissonClient constructRedissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort());
        return Redisson.create(config);
    }

    private Map<String, RedisCacheConfiguration> constructInitialCacheConfigurations() {
        final Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        final RedisCacheConfiguration baseCacheTtl = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(cacheProperties.getBaseCacheTtlMinutes()))
                .disableCachingNullValues();
        redisCacheConfigurationMap.put(PRODUCT_DETAIL, baseCacheTtl);
        redisCacheConfigurationMap.put(BROKER_DETAIL, baseCacheTtl);
        redisCacheConfigurationMap.put(CATEGORY_DETAIL, baseCacheTtl);
        redisCacheConfigurationMap.put(INVENTORY_ALL, baseCacheTtl);
        redisCacheConfigurationMap.put(INVENTORY_AVAILABLE, baseCacheTtl);
        redisCacheConfigurationMap.put(INVENTORY_CRITICAL, baseCacheTtl);
        redisCacheConfigurationMap.put(INVENTORY_OUT_OF, baseCacheTtl);
        redisCacheConfigurationMap.put(BROKER_BALANCE, baseCacheTtl);
        return redisCacheConfigurationMap;
    }
}

package com.defen.picflowbackend.manager;

import cn.hutool.core.util.RandomUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class CacheManager {

    // 缓存过期时间
    // 五分钟
    public static final long FIVE_MINUTES = 5 * 60;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取 5~10 分钟的随机缓存时间（秒）
     */
    public static long getRandomCacheTimeSeconds() {
        return FIVE_MINUTES + RandomUtil.randomInt(0, 300);
    }

    // 本地缓存
    private final Cache<String, String> LOCAL_CACHE =
            Caffeine.newBuilder().initialCapacity(1024)
                    .maximumSize(10000L)
                    // 缓存 5 分钟移除
                    .expireAfterWrite(5L, TimeUnit.MINUTES)
                    .build();

    /**
     * 获取本地缓存值
     *
     * @param key 缓存键
     * @return 缓存值
     */
    public String getLocalCache(String key) {
        return LOCAL_CACHE.getIfPresent(key);
    }

    public void setLocalCache(String key, String value) {
        LOCAL_CACHE.put(key, value);
    }

    /**
     * 从 Redis 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值（如果不存在返回 null）
     */
    public String gerRedisCache(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 存入缓存（随机时间）
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void setRedisCacheWithRandom(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value, getRandomCacheTimeSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 存入缓存（带过期时间）
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param timeout  过期时间
     * @param timeUnit 时间单位（例如 TimeUnit.MINUTES）
     */
    public void setRedisCache(String key, String value, long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 从本地或 Redis 获取缓存
     *
     * @param localKey 本地缓存 key
     * @param redisKey Redis 缓存 key
     * @return 缓存值（没有则返回 null）
     */
    public String getCache(String localKey, String redisKey) {
        // 1. 查本地缓存
        String localValue = LOCAL_CACHE.getIfPresent(localKey);
        if (localValue != null) {
            return localValue;
        }

        // 2. 查 Redis
        String redisValue = stringRedisTemplate.opsForValue().get(redisKey);
        if (redisValue != null) {
            // 回填本地缓存
            LOCAL_CACHE.put(localKey, redisValue);
        }
        return redisValue;
    }

    /**
     * 存入本地和 Redis 缓存（随机过期时间）
     *
     * @param localKey 本地缓存 key
     * @param redisKey Redis 缓存 key
     * @param value    缓存值
     */
    public void setCacheWithRandom(String localKey, String redisKey, String value) {
        // 写本地缓存
        LOCAL_CACHE.put(localKey, value);

        // 写 Redis 缓存（随机过期时间）
        long ttl = getRandomCacheTimeSeconds();
        stringRedisTemplate.opsForValue().set(redisKey, value, ttl, TimeUnit.SECONDS);
    }
}

package org.sbuf.service;

import org.sbuf.main.LoggingComponent;
import io.lettuce.core.RedisCommandTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@ConditionalOnExpression("${sbuf.service.smart-cache.enabled:false}")
public class CacheManagerService extends LoggingComponent<CacheManagerService> {

    @Autowired
    private CacheManager cacheManager;

//    @Autowired
//    private CacheSupport cacheSupport;

    @Value("${redis-swr.cache.key-prefix:null}")
    private String keyPrefix;

    public <T> Optional<T> find(String cacheKey, String key, Class<T> type) {
        Cache cache = cacheManager.getCache(cacheKey);
        if (cache == null) {
            return Optional.empty();
        }
        T cacheEntry = cache.get(key, type);
        if (cacheEntry == null) {
            return Optional.empty();
        }
        return Optional.of(cacheEntry);
    }

    public <T> Optional<T> findAndRemove(String cacheKey, String key, Class<T> type) {
        Optional<T> result = self.find(cacheKey, key, type);
        if (result.isPresent()) {
            self.remove(cacheKey, key);
        }

        return result;
    }

    public void put(String cacheKey, String key, Object value, Integer ttl) {
        try {
            Cache cache = cacheManager.getCache(cacheKey);

            if (cache != null) {
                self.log("save tokenInfo with key {}", key);
                cache.put(key, value);
                if (ttl != null) {
//                    cacheSupport.setTtl(keyPrefix, cacheKey, key, ttl);
                }
            }
        } catch (RedisCommandTimeoutException | QueryTimeoutException e) {
            self.logError("Error: {}", e.getMessage());
        }
    }

    public void remove(String cacheKey, String key) {
        try {
            Cache cache = cacheManager.getCache(cacheKey);

            if (cache != null && key != null) {
                self.log("evicting tokenInfo with uniqueId {}", key);
                cache.evict(key);
            }
        } catch (RedisCommandTimeoutException | QueryTimeoutException e) {
            self.logError("Error: {}", e.getMessage());
        }
    }
}

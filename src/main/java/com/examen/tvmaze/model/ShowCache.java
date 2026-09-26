package com.examen.tvmaze.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document (collection = "shows_cache")
public class ShowCache {

     @Id
    private Long id;

    private Map<String, Object> data;

    @Indexed(name = "cached_at_ttl", expireAfter = "24h")
    private Instant cachedAt;

    public ShowCache() {
    }

    public ShowCache(Long id, Map<String, Object> data) {
        this.id = id;
        this.data = data;
        this.cachedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Instant getCachedAt() {
        return cachedAt;
    }

    public void setCachedAt(Instant cachedAt) {
        this.cachedAt = cachedAt;
    }

}

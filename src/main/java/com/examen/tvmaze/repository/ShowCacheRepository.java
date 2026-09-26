package com.examen.tvmaze.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.examen.tvmaze.model.ShowCache;

public interface ShowCacheRepository extends MongoRepository<ShowCache, Long>{

}

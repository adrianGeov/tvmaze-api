package com.examen.tvmaze.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;


import org.springframework.stereotype.Service;

import com.examen.tvmaze.client.dto.TvMazeClient;
import com.examen.tvmaze.client.dto.TvMazeSearchResult;
import com.examen.tvmaze.dto.ShowSummaryResponse;
import com.examen.tvmaze.model.ShowCache;
import com.examen.tvmaze.repository.ShowCacheRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service 
public class ShowService {

    private static final Logger log = LoggerFactory.getLogger(ShowService.class);

    private final TvMazeClient tvMazeClient;
    private final ShowCacheRepository showCacheRepository;

    public ShowService(TvMazeClient tvMazeClient, ShowCacheRepository showCacheRepository) {
        this.tvMazeClient = tvMazeClient;
        this.showCacheRepository = showCacheRepository;
    }

    public List<ShowSummaryResponse> searchShows(String query) {
        return tvMazeClient.searchShows(query).stream()
                .map(TvMazeSearchResult::getShow)
                .filter(Objects::nonNull)
                .map(ShowSummaryResponse::from)
                .toList();
    }

    public Map<String, Object> getShow(Long showId) {
        return showCacheRepository.findById(showId)
                .map(ShowCache::getData)
                .orElseGet(() -> fetchAndCache(showId));
    }

    private Map<String, Object> fetchAndCache(Long showId) {
        log.info("Show {} no encontrado en cache, consultando TVMaze", showId);
        Map<String, Object> show = tvMazeClient.getShowById(showId);
        showCacheRepository.save(new ShowCache(showId, show));
        return show;
    }


}

package com.examen.tvmaze.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.examen.tvmaze.client.dto.TvMazeClient;
import com.examen.tvmaze.client.dto.TvMazeSearchResult;
import com.examen.tvmaze.client.dto.TvMazeShowSummary;
import com.examen.tvmaze.dto.CommentResponse;
import com.examen.tvmaze.dto.ShowSummaryResponse;
import com.examen.tvmaze.model.Comment;
import com.examen.tvmaze.model.ShowCache;
import com.examen.tvmaze.repository.CommentRepository;
import com.examen.tvmaze.repository.ShowCacheRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ShowService {

    private static final Logger log = LoggerFactory.getLogger(ShowService.class);

    private final TvMazeClient tvMazeClient;
    private final ShowCacheRepository showCacheRepository;
    private final CommentRepository commentRepository;

    public ShowService(TvMazeClient tvMazeClient,
            ShowCacheRepository showCacheRepository,
            CommentRepository commentRepository) {
        this.tvMazeClient = tvMazeClient;
        this.showCacheRepository = showCacheRepository;
        this.commentRepository = commentRepository;
    }

    public List<ShowSummaryResponse> searchShows(String query) {
        List<TvMazeShowSummary> shows = tvMazeClient.searchShows(query).stream()
                .map(TvMazeSearchResult::getShow)
                .filter(Objects::nonNull)
                .toList();

        if (shows.isEmpty()) {
            return List.of();
        }

        List<Long> showIds = shows.stream().map(TvMazeShowSummary::getId).toList();
        Map<Long, List<CommentResponse>> commentsByShow = findCommentsByShowIds(showIds);

        return shows.stream()
                .map(show -> ShowSummaryResponse.from(show,
                        commentsByShow.getOrDefault(show.getId(), List.of())))
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

    // Una sola consulta a Mongo para todos los shows del resultado (evita N+1)
    private Map<Long, List<CommentResponse>> findCommentsByShowIds(List<Long> showIds) {
        return commentRepository.findByShowIdIn(showIds).stream()
                .collect(Collectors.groupingBy(Comment::getShowId,
                        Collectors.mapping(CommentResponse::from, Collectors.toList())));
    }

}

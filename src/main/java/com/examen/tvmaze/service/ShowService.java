package com.examen.tvmaze.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.examen.tvmaze.client.dto.TvMazeClient;
import com.examen.tvmaze.client.dto.TvMazeSearchResult;
import com.examen.tvmaze.dto.ShowSummaryResponse;

@Service 
public class ShowService {

    private final TvMazeClient tvMazeClient;

    public ShowService(TvMazeClient tvMazeClient) {
        this.tvMazeClient = tvMazeClient;
    }


    public List<ShowSummaryResponse> searchShows(String query) {
        return tvMazeClient.searchShows(query).stream()
                .map(TvMazeSearchResult::getShow)
                .filter(Objects::nonNull)
                .map(ShowSummaryResponse::from)
                .toList();
    }

    public Map<String, Object> getShow(Long showId) {
        return tvMazeClient.getShowById(showId);
    }


}

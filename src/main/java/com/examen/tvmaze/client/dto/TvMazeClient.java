package com.examen.tvmaze.client.dto;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.examen.tvmaze.exception.ExternalServiceException;


@Component 
public class TvMazeClient {

    private final RestClient restClient;


    public TvMazeClient(RestClient.Builder builder, @Value("${tvmaze.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public List<TvMazeSearchResult> searchShows(String query) {
        try {
            TvMazeSearchResult[] results = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/search/shows").queryParam("q", query).build())
                    .retrieve()
                    .body(TvMazeSearchResult[].class);
            return results == null ? List.of() : List.of(results);
        } catch (RestClientException ex) {
            throw new ExternalServiceException("Error al consultar la busqueda en TVMaze", ex);
        }
    }

}

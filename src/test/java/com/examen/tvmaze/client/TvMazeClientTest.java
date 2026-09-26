package com.examen.tvmaze.client;

import java.util.List;
import java.util.Map;

import com.examen.tvmaze.client.dto.TvMazeClient;
import com.examen.tvmaze.client.dto.TvMazeSearchResult;
import com.examen.tvmaze.exception.ExternalServiceException;
import com.examen.tvmaze.exception.ShowNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;



import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;


@RestClientTest (TvMazeClient.class)
class TvMazeClientTest {


     @Autowired
    private TvMazeClient tvMazeClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void searchShows_mapeaRespuestaDeTvMaze() {
        String json = """
                [{"score":0.9,"show":{"id":139,"name":"Girls","summary":"<p>Resumen</p>",
                "genres":["Drama"],"network":{"name":"HBO"},"webChannel":null}}]
                """;
        server.expect(requestTo("https://api.tvmaze.com/search/shows?q=girls"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<TvMazeSearchResult> results = tvMazeClient.searchShows("girls");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getShow().getName()).isEqualTo("Girls");
        assertThat(results.get(0).getShow().resolveChannelName()).isEqualTo("HBO");
    }

    @Test
    void searchShows_cuandoTvMazeFalla_lanzaExternalServiceException() {
        server.expect(requestTo("https://api.tvmaze.com/search/shows?q=girls"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> tvMazeClient.searchShows("girls"))
                .isInstanceOf(ExternalServiceException.class);
    }


    @Test
    void getShowById_regresaShowCompleto() {
        server.expect(requestTo("https://api.tvmaze.com/shows/1"))
                .andRespond(withSuccess("{\"id\":1,\"name\":\"Under the Dome\",\"language\":\"English\"}",
                        MediaType.APPLICATION_JSON));

        Map<String, Object> show = tvMazeClient.getShowById(1L);

        assertThat(show)
                .containsEntry("name", "Under the Dome")
                .containsEntry("language", "English");
    }

    @Test
    void getShowById_cuandoTvMazeRegresa404_lanzaShowNotFound() {
        server.expect(requestTo("https://api.tvmaze.com/shows/999999"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> tvMazeClient.getShowById(999999L))
                .isInstanceOf(ShowNotFoundException.class);
    }




}

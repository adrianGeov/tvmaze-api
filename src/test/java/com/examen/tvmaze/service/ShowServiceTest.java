package com.examen.tvmaze.service;

import com.examen.tvmaze.client.dto.TvMazeChannel;
import com.examen.tvmaze.client.dto.TvMazeClient;
import com.examen.tvmaze.client.dto.TvMazeSearchResult;
import com.examen.tvmaze.client.dto.TvMazeShowSummary;
import com.examen.tvmaze.dto.ShowSummaryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShowServiceTest {

    @Mock
    private TvMazeClient tvMazeClient;

    @InjectMocks
    private ShowService showService;

    @Test
    void searchShows_usaNetworkComoCanal() {
        TvMazeShowSummary show = new TvMazeShowSummary(139L, "Girls", "<p>Resumen</p>", List.of("Drama"),
                new TvMazeChannel("HBO"), null);
        when(tvMazeClient.searchShows("girls")).thenReturn(List.of(new TvMazeSearchResult(0.9, show)));

        List<ShowSummaryResponse> result = showService.searchShows("girls");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChannel()).isEqualTo("HBO");
    }

    @Test
    void searchShows_sinNetwork_usaWebChannel() {
        TvMazeShowSummary show = new TvMazeShowSummary(1L, "Show web", null, null,
                null, new TvMazeChannel("Netflix"));
        when(tvMazeClient.searchShows("web")).thenReturn(List.of(new TvMazeSearchResult(0.5, show)));

        List<ShowSummaryResponse> result = showService.searchShows("web");

        assertThat(result.get(0).getChannel()).isEqualTo("Netflix");
        assertThat(result.get(0).getGenres()).isEmpty();
    }

    @Test
    void searchShows_sinResultados_regresaListaVacia() {
        when(tvMazeClient.searchShows("zzz")).thenReturn(List.of());

        assertThat(showService.searchShows("zzz")).isEmpty();
    }

    @Test
    void getShow_delegaAlCliente() {
        Map<String, Object> fromApi = Map.of("id", 1, "name", "Under the Dome");
        when(tvMazeClient.getShowById(1L)).thenReturn(fromApi);

        assertThat(showService.getShow(1L)).isEqualTo(fromApi);
    }

}

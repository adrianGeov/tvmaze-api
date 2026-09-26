package com.examen.tvmaze.service;

import com.examen.tvmaze.client.dto.TvMazeChannel;
import com.examen.tvmaze.client.dto.TvMazeClient;
import com.examen.tvmaze.client.dto.TvMazeSearchResult;
import com.examen.tvmaze.client.dto.TvMazeShowSummary;
import com.examen.tvmaze.dto.ShowSummaryResponse;
import com.examen.tvmaze.exception.ShowNotFoundException;
import com.examen.tvmaze.model.ShowCache;
import com.examen.tvmaze.repository.ShowCacheRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShowServiceTest {

    @Mock
    private TvMazeClient tvMazeClient;

     @Mock
    private ShowCacheRepository showCacheRepository;

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
    void getShow_cuandoExisteEnCache_noConsultaTvMaze() {
        Map<String, Object> cached = Map.of("id", 1, "name", "Under the Dome");
        when(showCacheRepository.findById(1L)).thenReturn(Optional.of(new ShowCache(1L, cached)));

        Map<String, Object> result = showService.getShow(1L);

        assertThat(result).containsEntry("name", "Under the Dome");
        verify(tvMazeClient, never()).getShowById(any());
        verify(showCacheRepository, never()).save(any());
    }

    @Test
    void getShow_cuandoNoExisteEnCache_consultaTvMazeYGuarda() {
        Map<String, Object> fromApi = Map.of("id", 1, "name", "Under the Dome");
        when(showCacheRepository.findById(1L)).thenReturn(Optional.empty());
        when(tvMazeClient.getShowById(1L)).thenReturn(fromApi);

        Map<String, Object> result = showService.getShow(1L);

        assertThat(result).isEqualTo(fromApi);
        ArgumentCaptor<ShowCache> captor = ArgumentCaptor.forClass(ShowCache.class);
        verify(showCacheRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(1L);
        assertThat(captor.getValue().getData()).isEqualTo(fromApi);
        assertThat(captor.getValue().getCachedAt()).isNotNull();
    }

    @Test
    void getShow_cuandoShowNoExisteEnTvMaze_noGuardaEnCache() {
        when(showCacheRepository.findById(999L)).thenReturn(Optional.empty());
        when(tvMazeClient.getShowById(999L)).thenThrow(new ShowNotFoundException(999L));

        assertThatThrownBy(() -> showService.getShow(999L))
                .isInstanceOf(ShowNotFoundException.class);
        verify(showCacheRepository, never()).save(any());
    }

}

package com.examen.tvmaze.service;

import com.examen.tvmaze.client.dto.TvMazeChannel;
import com.examen.tvmaze.client.dto.TvMazeClient;
import com.examen.tvmaze.client.dto.TvMazeSearchResult;
import com.examen.tvmaze.client.dto.TvMazeShowSummary;
import com.examen.tvmaze.dto.CommentResponse;
import com.examen.tvmaze.dto.ShowSummaryResponse;
import com.examen.tvmaze.exception.ShowNotFoundException;
import com.examen.tvmaze.model.Comment;
import com.examen.tvmaze.model.ShowCache;
import com.examen.tvmaze.repository.CommentRepository;
import com.examen.tvmaze.repository.ShowCacheRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShowServiceTest {

   @Mock
    private TvMazeClient tvMazeClient;

    @Mock
    private ShowCacheRepository showCacheRepository;

    @Mock
    private CommentRepository commentRepository;

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
        assertThat(result.get(0).getComments()).isEmpty();
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
    void searchShows_agregaComentariosPorShow() {
        TvMazeShowSummary girls = new TvMazeShowSummary(139L, "Girls", null, List.of("Drama"),
                new TvMazeChannel("HBO"), null);
        TvMazeShowSummary other = new TvMazeShowSummary(200L, "Otro", null, List.of(),
                new TvMazeChannel("CBS"), null);
        when(tvMazeClient.searchShows("girls")).thenReturn(List.of(
                new TvMazeSearchResult(0.9, girls),
                new TvMazeSearchResult(0.5, other)));
        when(commentRepository.findByShowIdIn(anyCollection())).thenReturn(List.of(
                new Comment(139L, "Buena serie", 4),
                new Comment(139L, "Me encanto", 5)));

        List<ShowSummaryResponse> result = showService.searchShows("girls");

        assertThat(result.get(0).getComments()).hasSize(2);
        assertThat(result.get(0).getComments().get(0).getRating()).isEqualTo(4);
        assertThat(result.get(1).getComments()).isEmpty();
        verify(commentRepository, times(1)).findByShowIdIn(anyCollection());
    }

    @Test
    void searchShows_sinResultados_noConsultaComentarios() {
        when(tvMazeClient.searchShows("zzz")).thenReturn(List.of());

        assertThat(showService.searchShows("zzz")).isEmpty();
        verify(commentRepository, never()).findByShowIdIn(anyCollection());
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

    @Test
    void getShowWithComments_agregaComentariosAlShow() {
        when(showCacheRepository.findById(1L))
                .thenReturn(Optional.of(new ShowCache(1L, Map.of("id", 1, "name", "Under the Dome"))));
        when(commentRepository.findByShowIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(new Comment(1L, "Me encanto", 5)));

        Map<String, Object> result = showService.getShowWithComments(1L);

        assertThat(result).containsEntry("name", "Under the Dome");
        List<?> comments = (List<?>) result.get("comments");
        assertThat(comments).hasSize(1);
        CommentResponse comment = (CommentResponse) comments.get(0);
        assertThat(comment.getComment()).isEqualTo("Me encanto");
        assertThat(comment.getRating()).isEqualTo(5);
    }

    @Test
    void getShowWithComments_noModificaLosDatosDeLaCache() {
        Map<String, Object> cachedData = new HashMap<>(Map.of("id", 1, "name", "Under the Dome"));
        when(showCacheRepository.findById(1L)).thenReturn(Optional.of(new ShowCache(1L, cachedData)));
        when(commentRepository.findByShowIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());

        showService.getShowWithComments(1L);

        assertThat(cachedData).doesNotContainKey("comments");
    }

}

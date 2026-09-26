package com.examen.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.examen.tvmaze.dto.CommentRequest;
import com.examen.tvmaze.exception.ShowNotFoundException;
import com.examen.tvmaze.model.Comment;
import com.examen.tvmaze.repository.CommentRepository;

@ExtendWith (MockitoExtension.class)
public class CommentServiceTest {

 @Mock
    private CommentRepository commentRepository;

    @Mock
    private ShowService showService;

    @InjectMocks
    private CommentService commentService;

    @Test
    void addComment_cuandoElShowExiste_guardaComentario() {
        when(showService.getShow(1L)).thenReturn(Map.of("id", 1));

        commentService.addComment(1L, new CommentRequest("  Excelente serie  ", 5));

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        Comment saved = captor.getValue();
        assertThat(saved.getShowId()).isEqualTo(1L);
        assertThat(saved.getComment()).isEqualTo("Excelente serie");
        assertThat(saved.getRating()).isEqualTo(5);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void addComment_cuandoElShowNoExiste_noGuarda() {
        when(showService.getShow(999L)).thenThrow(new ShowNotFoundException(999L));

        assertThatThrownBy(() -> commentService.addComment(999L, new CommentRequest("Hola", 3)))
                .isInstanceOf(ShowNotFoundException.class);
        verify(commentRepository, never()).save(any());
    }

}

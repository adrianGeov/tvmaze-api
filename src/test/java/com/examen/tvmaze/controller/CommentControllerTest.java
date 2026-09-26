package com.examen.tvmaze.controller;

import com.examen.tvmaze.dto.CommentRequest;
import com.examen.tvmaze.exception.GlobalExceptionHandler;
import com.examen.tvmaze.exception.ShowNotFoundException;
import com.examen.tvmaze.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import(GlobalExceptionHandler.class)
public class CommentControllerTest {

    private static final String URL = "/api/shows/1/comments";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    void addComment_valido_regresa201() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"comment\":\"Muy buena\",\"rating\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"));

        verify(commentService).addComment(eq(1L), any(CommentRequest.class));
    }

    @Test
    void addComment_ratingMayorA5_regresa400() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"comment\":\"Muy buena\",\"rating\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").value("rating: La calificacion maxima es 5"));

        verify(commentService, never()).addComment(any(), any());
    }

    @Test
    void addComment_ratingNegativo_regresa400() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"comment\":\"Muy buena\",\"rating\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").value("rating: La calificacion minima es 0"));
    }

    @Test
    void addComment_ratingNulo_regresa400() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"comment\":\"Muy buena\",\"rating\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").value("rating: La calificacion es obligatoria"));

        verify(commentService, never()).addComment(any(), any());
    }

    @Test
    void addComment_comentarioVacio_regresa400() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"comment\":\"   \",\"rating\":3}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").value("comment: El comentario es obligatorio"));
    }

    @Test
    void addComment_jsonMalFormado_regresa400() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"comment\":"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_showNoExiste_regresa404() throws Exception {
        doThrow(new ShowNotFoundException(1L))
                .when(commentService).addComment(eq(1L), any(CommentRequest.class));

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"comment\":\"Muy buena\",\"rating\":4}"))
                .andExpect(status().isNotFound());
    }

}

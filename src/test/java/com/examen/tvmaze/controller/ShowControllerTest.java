package com.examen.tvmaze.controller;

import com.examen.tvmaze.dto.ShowSummaryResponse;
import com.examen.tvmaze.exception.ExternalServiceException;
import com.examen.tvmaze.service.ShowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(ShowController.class)
public class ShowControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShowService showService;

    @Test
    void search_regresaArregloDeShows() throws Exception {
        when(showService.searchShows("girls")).thenReturn(List.of(
                new ShowSummaryResponse(139L, "Girls", "HBO", "<p>Resumen</p>", List.of("Drama"))));

        mockMvc.perform(get("/api/shows/search").param("q", "girls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(139))
                .andExpect(jsonPath("$[0].channel").value("HBO"));
    }

    @Test
    void search_criterioVacio_regresa400() throws Exception {
        mockMvc.perform(get("/api/shows/search").param("q", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void search_sinParametro_regresa400() throws Exception {
        mockMvc.perform(get("/api/shows/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_cuandoTvMazeFalla_regresa502SinStackTrace() throws Exception {
        when(showService.searchShows("girls"))
                .thenThrow(new ExternalServiceException("timeout", new RuntimeException("detalle interno")));

        mockMvc.perform(get("/api/shows/search").param("q", "girls"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(content().string(not(containsString("detalle interno"))));
    }

}

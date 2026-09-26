package com.examen.tvmaze.controller;

import com.examen.tvmaze.dto.ShowSummaryResponse;
import com.examen.tvmaze.exception.ExternalServiceException;
import com.examen.tvmaze.exception.ShowNotFoundException;
import com.examen.tvmaze.service.ShowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

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

    @Test
    void getShow_regresaShowCompleto() throws Exception {
        when(showService.getShow(1L)).thenReturn(Map.of("id", 1, "name", "Under the Dome"));

        mockMvc.perform(get("/api/shows/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Under the Dome"));
    }

    @Test
    void getShow_noExistente_regresa404Estandarizado() throws Exception {
        when(showService.getShow(999L)).thenThrow(new ShowNotFoundException(999L));

        mockMvc.perform(get("/api/shows/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No se encontro el show con id 999"));
    }

    @Test
    void getShow_idNoNumerico_regresa400() throws Exception {
        mockMvc.perform(get("/api/shows/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getShow_idNegativo_regresa400() throws Exception {
        mockMvc.perform(get("/api/shows/-5"))
                .andExpect(status().isBadRequest());
    }

}

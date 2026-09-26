package com.examen.tvmaze.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.examen.tvmaze.dto.ShowSummaryResponse;
import com.examen.tvmaze.service.ShowService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@RestController 
@RequestMapping ("/api/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<ShowSummaryResponse>> search(
            @RequestParam("q")
            @NotBlank(message = "El criterio de busqueda es obligatorio")
            @Size(max = 100, message = "El criterio de busqueda no debe exceder 100 caracteres")
            String query) {
        return ResponseEntity.ok(showService.searchShows(query.trim()));
    }

    @GetMapping("/{showId}")
    public ResponseEntity<Map<String, Object>> getShow(
            @PathVariable @Positive(message = "El id del show debe ser positivo") Long showId) {
        return ResponseEntity.ok(showService.getShow(showId));
    }

}

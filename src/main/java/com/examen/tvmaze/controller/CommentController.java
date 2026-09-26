package com.examen.tvmaze.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examen.tvmaze.dto.CommentRequest;
import com.examen.tvmaze.dto.StatusResponse;
import com.examen.tvmaze.service.CommentService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/api/shows/{showId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<StatusResponse> addComment(@PathVariable Long showId,
                                                     @Valid @RequestBody CommentRequest request) {
        commentService.addComment(showId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StatusResponse.success("Comentario registrado correctamente"));
    }

}

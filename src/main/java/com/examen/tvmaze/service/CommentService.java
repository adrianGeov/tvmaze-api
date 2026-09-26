package com.examen.tvmaze.service;

import org.springframework.stereotype.Service;

import com.examen.tvmaze.dto.CommentRequest;
import com.examen.tvmaze.model.Comment;
import com.examen.tvmaze.repository.CommentRepository;

@Service 
public class CommentService {

     private final CommentRepository commentRepository;
    private final ShowService showService;

    public CommentService(CommentRepository commentRepository, ShowService showService) {
        this.commentRepository = commentRepository;
        this.showService = showService;
    }

    public void addComment(Long showId, CommentRequest request) {
        showService.getShow(showId);
        commentRepository.save(new Comment(showId, request.getComment().trim(), request.getRating()));
    }

}

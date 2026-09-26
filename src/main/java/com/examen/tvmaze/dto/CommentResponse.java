package com.examen.tvmaze.dto;

import com.examen.tvmaze.model.Comment;

public class CommentResponse {

    private final String comment;
    private final Integer rating;

    public CommentResponse(String comment, Integer rating) {
        this.comment = comment;
        this.rating = rating;
    }

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(comment.getComment(), comment.getRating());
    }

    public String getComment() {
        return comment;
    }

    public Integer getRating() {
        return rating;
    }

}

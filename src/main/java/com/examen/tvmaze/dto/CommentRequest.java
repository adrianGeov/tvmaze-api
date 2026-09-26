package com.examen.tvmaze.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentRequest {

    @NotBlank(message = "El comentario es obligatorio")
    @Size(max = 500, message = "El comentario no debe exceder 500 caracteres")
    private String comment;

    @NotNull(message = "La calificacion es obligatoria")
    @Min(value = 0, message = "La calificacion minima es 0")
    @Max(value = 5, message = "La calificacion maxima es 5")
    private Integer rating;

    public CommentRequest() {
    }

    public CommentRequest(String comment, Integer rating) {
        this.comment = comment;
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

}

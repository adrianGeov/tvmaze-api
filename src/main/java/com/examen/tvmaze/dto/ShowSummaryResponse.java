package com.examen.tvmaze.dto;

import java.util.List;

import com.examen.tvmaze.client.dto.TvMazeShowSummary;

public class ShowSummaryResponse {


    private final Long id;
    private final String name;
    private final String channel;
    private final String summary;
    private final List<String> genres;
     private final List<CommentResponse> comments;

    public ShowSummaryResponse(Long id, String name, String channel, String summary, List<String> genres, List<CommentResponse> comments) {
        this.id = id;
        this.name = name;
        this.channel = channel;
        this.summary = summary;
        this.genres = genres;
        this.comments = comments;
    }

    public static ShowSummaryResponse from(TvMazeShowSummary show, List<CommentResponse> comments) {
        return new ShowSummaryResponse(
                show.getId(),
                show.getName(),
                show.resolveChannelName(),
                show.getSummary(),
                show.getGenres() == null ? List.of() : show.getGenres(),
                comments);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getChannel() {
        return channel;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<CommentResponse> getComments() {
        return comments;
    }


}

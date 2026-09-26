package com.examen.tvmaze.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties (ignoreUnknown = true)
public class TvMazeSearchResult {


    private Double score;
    private TvMazeShowSummary show;


public TvMazeSearchResult() {
    }

    public TvMazeSearchResult(Double score, TvMazeShowSummary show) {
        this.score = score;
        this.show = show;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public TvMazeShowSummary getShow() {
        return show;
    }

    public void setShow(TvMazeShowSummary show) {
        this.show = show;
    }



}

package com.examen.tvmaze.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TvMazeShowSummary {

    private Long id;
    private String name;
    private String summary;
    private List<String> genres;
    private TvMazeChannel network;
    private TvMazeChannel webChannel;

    public TvMazeShowSummary() {
    }

    public TvMazeShowSummary(Long id, String name, String summary, List<String> genres,
            TvMazeChannel network, TvMazeChannel webChannel) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.genres = genres;
        this.network = network;
        this.webChannel = webChannel;
    }

    public String resolveChannelName() {
        if (network != null) {
            return network.getName();
        }
        return webChannel != null ? webChannel.getName() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public TvMazeChannel getNetwork() {
        return network;
    }

    public void setNetwork(TvMazeChannel network) {
        this.network = network;
    }

    public TvMazeChannel getWebChannel() {
        return webChannel;
    }

    public void setWebChannel(TvMazeChannel webChannel) {
        this.webChannel = webChannel;
    }
}
package com.examen.tvmaze.dto;

public class StatusResponse {

    private final String status;
    private final String message;

    public StatusResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public static StatusResponse success(String message) {
        return new StatusResponse("success", message);
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}

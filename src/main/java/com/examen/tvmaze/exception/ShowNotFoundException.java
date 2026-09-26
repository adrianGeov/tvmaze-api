package com.examen.tvmaze.exception;

public class ShowNotFoundException extends RuntimeException {

    public ShowNotFoundException(Long showId) {
        super("No se encontro el show con id " + showId);
    }

}

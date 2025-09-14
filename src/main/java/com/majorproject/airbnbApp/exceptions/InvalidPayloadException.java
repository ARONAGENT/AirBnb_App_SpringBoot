package com.majorproject.airbnbApp.exceptions;

import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidPayloadException extends RuntimeException{

    private Map<String, String> errors;
    public InvalidPayloadException(String message, Map<String, String> errors) {
        super(message); this.errors = errors;
    }
}

package com.mycompany.jobhunter.utils.errors;

public class InvalidRequestBodyException extends Exception {
    public InvalidRequestBodyException(String message) {
        super(message);
    }
}

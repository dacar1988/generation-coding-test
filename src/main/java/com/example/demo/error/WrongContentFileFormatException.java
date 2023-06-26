package com.example.demo.error;

public class WrongContentFileFormatException extends RuntimeException {
    public WrongContentFileFormatException(String message) {
        super(message);
    }
}

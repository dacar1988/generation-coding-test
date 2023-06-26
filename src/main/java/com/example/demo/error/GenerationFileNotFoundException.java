package com.example.demo.error;

public class GenerationFileNotFoundException extends RuntimeException {
    public GenerationFileNotFoundException(String message) {
        super(message);
    }
}

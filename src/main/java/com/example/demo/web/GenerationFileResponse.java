package com.example.demo.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class GenerationFileResponse {

    @JsonProperty
    private Long id;
    @JsonProperty
    private String name;
    @JsonProperty
    private byte[] data;
}
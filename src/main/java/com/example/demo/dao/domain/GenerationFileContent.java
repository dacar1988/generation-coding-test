package com.example.demo.dao.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class GenerationFileContent {
    @JsonProperty
    private Long valueX;
    @JsonProperty
    private Long valueY;
}
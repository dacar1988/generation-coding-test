package com.example.demo.dao.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GenerationFileContentResult extends GenerationFileContent {
    @JsonProperty
    private Long result;

    public GenerationFileContentResult(Long valueX, Long valueY, Long result) {
        super(valueX, valueY);
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenerationFileContentResult that)) return false;
        if (!super.equals(o)) return false;
        return getResult().equals(that.getResult());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getResult());
    }
}
package com.example.demo.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetGenerationFileResponse {

    private Long id;
    private String name;
    private Resource resource;
}

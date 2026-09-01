package com.example.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProductDTO {

    @NotBlank
    private String description;
}
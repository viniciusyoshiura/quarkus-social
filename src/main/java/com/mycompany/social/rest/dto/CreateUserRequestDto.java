package com.mycompany.social.rest.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateUserRequestDto {

    @NotBlank
    private String name;

    @NotNull
    private Integer age;

}

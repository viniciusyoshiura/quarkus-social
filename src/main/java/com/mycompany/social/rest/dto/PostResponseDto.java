package com.mycompany.social.rest.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponseDto {

    private String text;

    private LocalDateTime date_time;

}

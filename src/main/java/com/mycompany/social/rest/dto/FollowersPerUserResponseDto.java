package com.mycompany.social.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FollowersPerUserResponseDto {

    private Integer followers_count;
    private List<FollowerResponseDto> followers;

}

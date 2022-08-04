package com.mycompany.social.rest.dto;

import com.mycompany.social.domain.entity.FollowerEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FollowerResponseDto {

    private Long id;
    private String name;

    public FollowerResponseDto(FollowerEntity follower){
        this(follower.getId(), follower.getFollower().getName());
    }

}

package com.mycompany.social.rest.dto.converter;

import com.mycompany.social.domain.entity.PostEntity;
import com.mycompany.social.rest.dto.PostResponseDto;

public class PostResponseDtoConverter {

    public static PostResponseDto fromEntity(PostEntity post){
        PostResponseDto response = new PostResponseDto();
        response.setText(post.getText());
        response.setDate_time(post.getDateTime());
        return response;
    }

}

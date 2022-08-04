package com.mycompany.social.rest;

import com.mycompany.social.domain.entity.PostEntity;
import com.mycompany.social.domain.entity.UserEntity;
import com.mycompany.social.domain.repository.FollowerRepository;
import com.mycompany.social.domain.repository.PostRepository;
import com.mycompany.social.domain.repository.UserRepository;
import com.mycompany.social.rest.dto.CreatePostRequestDto;
import com.mycompany.social.rest.dto.PostResponseDto;
import com.mycompany.social.rest.dto.converter.PostResponseDtoConverter;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostController {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private FollowerRepository followerRepository;

    @Inject
    public PostController(UserRepository userRepository, PostRepository postRepository, FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId,
                             CreatePostRequestDto request){

        UserEntity user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        PostEntity post = new PostEntity();
        post.setText(request.getText());
        post.setUser(user);

        postRepository.persist(post);

        return Response.status(Response.Status.CREATED).build();

    }

    @GET
    public Response listPosts(
            @PathParam("userId") Long userId,
            @HeaderParam("follower_id") Long followerId){

        UserEntity user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(followerId == null){
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("You forgot the header follower_id")
                    .build();
        }

        UserEntity follower = userRepository.findById(followerId);

        if(follower == null){
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Follower does not exist")
                    .build();
        }

        boolean follows = followerRepository.follows(follower, user);
        if(!follows){
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You cannot see these posts, since you are not a follower!")
                    .build();
        }

        PanacheQuery<PostEntity> postQuery = postRepository.find(
                "user", Sort.by("dateTime", Sort.Direction.Descending), user);

        List<PostEntity> listPosts = postQuery.list();

        List<PostResponseDto> responseList = listPosts.stream()
                .map(PostResponseDtoConverter::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(responseList).build();
    }


}

package com.mycompany.social.rest;

import com.mycompany.social.domain.entity.FollowerEntity;
import com.mycompany.social.domain.entity.UserEntity;
import com.mycompany.social.domain.repository.FollowerRepository;
import com.mycompany.social.domain.repository.UserRepository;
import com.mycompany.social.rest.dto.FollowerRequestDto;
import com.mycompany.social.rest.dto.FollowerResponseDto;
import com.mycompany.social.rest.dto.FollowersPerUserResponseDto;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerController {

    private FollowerRepository followerRepository;
    private UserRepository userRepository;

    @Inject
    public FollowerController(
            FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followUser(
            @PathParam("userId") Long userId,
            FollowerRequestDto request){

        if(userId.equals(request.getFollower_id())){
            return Response.status(Response.Status.CONFLICT)
                    .entity("You cannot follow yourself")
                    .build();
        }

        UserEntity user = userRepository.findById(userId);

        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        UserEntity userFollower = userRepository.findById(request.getFollower_id());
        if(userFollower == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }


        boolean follows = followerRepository.follows(userFollower, user);

        if(!follows){
            FollowerEntity followerEntity = new FollowerEntity();
            followerEntity.setUser(user);
            followerEntity.setFollower(userFollower);

            followerRepository.persist(followerEntity);
        } else {
            return Response.status(Response.Status.CONFLICT)
                    .entity("You already follow this user")
                    .build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();

    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId){

        UserEntity user = userRepository.findById(userId);

        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<FollowerEntity> listOfFollowersEntity = followerRepository.findByUser(userId);

        FollowersPerUserResponseDto responseObject = new FollowersPerUserResponseDto();
        responseObject.setFollowers_count(listOfFollowersEntity.size());

        List<FollowerResponseDto> followerResponseDtosList = listOfFollowersEntity
                .stream()
                .map(FollowerResponseDto::new)
                .collect(Collectors.toList());

        responseObject.setFollowers(followerResponseDtosList);
        return Response.ok(responseObject).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(
            @PathParam("userId") Long userId,
            @QueryParam("follower_id")  Long followerId ){

        UserEntity user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        UserEntity follower = userRepository.findById(followerId);
        if(follower == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteByFollowerAndUser(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}

package com.mycompany.social.rest;

import com.mycompany.social.domain.entity.FollowerEntity;
import com.mycompany.social.domain.entity.UserEntity;
import com.mycompany.social.domain.repository.FollowerRepository;
import com.mycompany.social.domain.repository.UserRepository;
import com.mycompany.social.rest.dto.FollowerRequestDto;
import com.mycompany.social.rest.dto.FollowerResponseDto;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.restassured.http.ContentType;
import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerController.class)
public class FollowerControllerTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;
    Long followerToTestId;

    @BeforeEach
    @Transactional
    void setUp() {

        UserEntity user = new UserEntity();
        user.setAge(30);
        user.setName("Test 1");
        userRepository.persist(user);
        userId = user.getId();

        UserEntity follower = new UserEntity();
        follower.setAge(31);
        follower.setName("Test 2");
        userRepository.persist(follower);
        followerId = follower.getId();

        UserEntity followerToTest = new UserEntity();
        followerToTest.setAge(56);
        followerToTest.setName("Test 3");
        userRepository.persist(followerToTest);
        followerToTestId = followerToTest.getId();

        FollowerEntity followerEntity = new FollowerEntity();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("should return 409 when Follower Id is equal to User id")
    public void sameUserAsFollowerTest(){

        FollowerRequestDto body = new FollowerRequestDto();
        body.setFollower_id(userId);

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(Response.Status.CONFLICT.getStatusCode())
            .body(Matchers.is("You cannot follow yourself"));
    }

    @Test
    @DisplayName("should return 404 on follow a user when User id doen't exist")
    public void userNotFoundWhenTryingToFollowTest(){

        FollowerRequestDto body = new FollowerRequestDto();
        body.setFollower_id(userId);

        Long nonexistentUserId = 999L;

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", nonexistentUserId)
        .when()
             .put()
             .then()
             .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should follow a user")
    public void followUserTest(){

        FollowerRequestDto body = new FollowerRequestDto();
        body.setFollower_id(followerToTestId);

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 on list user followers and User id does not exist")
    public void userNotFoundWhenListingFollowersTest(){
        var nonexistentUserId = 999;

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", nonexistentUserId)
        .when()
            .get()
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should list a user's followers")
    public void listFollowersTest(){
        io.restassured.response.Response response =
            given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
            .when()
                .get()
            .then()
                .extract().response();

        Integer followersCount = response.jsonPath().get("followers_count");
        List<FollowerResponseDto> followers = response.jsonPath().getList("followers");

        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followers.size());

    }

    @Test
    @DisplayName("should return 404 on unfollow user and User id doen't exist")
    public void userNotFoundWhenUnfollowingAUserTest(){
        Long nonexistentUserId = 999L;

        given()
            .pathParam("userId", nonexistentUserId)
            .queryParam("followerId", followerId)
        .when()
            .delete()
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should Unfollow an user")
    public void unfollowUserTest(){
        given()
            .pathParam("userId", userId)
            .queryParam("follower_id", followerId)
        .when()
            .delete()
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

}

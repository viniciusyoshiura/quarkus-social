package com.mycompany.social.rest;

import com.mycompany.social.domain.entity.FollowerEntity;
import com.mycompany.social.domain.entity.PostEntity;
import com.mycompany.social.domain.entity.UserEntity;
import com.mycompany.social.domain.repository.FollowerRepository;
import com.mycompany.social.domain.repository.PostRepository;
import com.mycompany.social.domain.repository.UserRepository;
import com.mycompany.social.rest.dto.CreatePostRequestDto;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostController.class)
public class PostControllerTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setUP(){

        UserEntity user = new UserEntity();
        user.setAge(30);
        user.setName("Test 1");
        userRepository.persist(user);
        userId = user.getId();

        PostEntity post = new PostEntity();
        post.setText("Meu primeiro post");
        post.setUser(user);
        postRepository.persist(post);

        UserEntity userNotFollower = new UserEntity();
        userNotFollower.setAge(33);
        userNotFollower.setName("Test 2");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        UserEntity userFollower = new UserEntity();
        userFollower.setAge(31);
        userFollower.setName("Test 3");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        FollowerEntity follower = new FollowerEntity();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);

    }

    @Test
    @DisplayName("should create a post for a user")
    public void createPostTest(){
        CreatePostRequestDto postRequest = new CreatePostRequestDto();
        postRequest.setText("Meu primeiro post");

        given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .pathParam("userId", userId)
        .when()
            .post()
        .then()
            .statusCode(201);
    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an nonexistent user")
    public void postForAnNonExistentUserTest(){
        CreatePostRequestDto postRequest = new CreatePostRequestDto();
        postRequest.setText("Meu primeiro post");

        Long nonexistentUserId = 999L;

        given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .pathParam("userId", nonexistentUserId)
        .when()
            .post()
        .then()
             .statusCode(404);
    }

    @Test
    @DisplayName("should return 404 when user does not exist")
    public void listPostUserNotFoundTest(){
        Long nonexistentUserId = 999L;

        given()
            .pathParam("userId", nonexistentUserId)
        .when()
            .get()
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should return 400 when followerId header is not present")
    public void listPostFollowerHeaderNotSendTest(){
        given()
            .pathParam("userId", userId)
        .when()
            .get()
            .then()
        .statusCode(400)
            .body(Matchers.is("You forgot the header follower_id"));
    }

    @Test
    @DisplayName("should return 400 when follower does not exist")
    public void listPostFollowerNotFoundTest(){

        Long nonexistentFollowerId = 999L;

        given()
            .pathParam("userId", userId)
            .header("follower_id", nonexistentFollowerId)
        .when()
            .get()
        .then()
            .statusCode(400)
            .body(Matchers.is("Follower does not exist"));
    }

    @Test
    @DisplayName("should return 403 when follower is not a follower")
    public void listPostNotAFollower(){
        given()
            .pathParam("userId", userId)
            .header("follower_id", userNotFollowerId)
        .when()
            .get()
        .then()
            .statusCode(403)
            .body(Matchers.is("You cannot see these posts, since you are not a follower!"));
    }

    @Test
    @DisplayName("should list posts")
    public void listPostsTest(){
        given()
            .pathParam("userId", userId)
            .header("follower_id", userFollowerId)
        .when()
            .get()
            .then()
            .statusCode(200)
            .body("size()", Matchers.is(1));
    }

}

package com.mycompany.social.rest;

import com.mycompany.social.rest.dto.CreateUserRequestDto;
import com.mycompany.social.rest.dto.ResponseErrorDto;
import io.quarkus.test.common.http.TestHTTPResource;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @DisplayName("should create an user successfully")
    @Order(1)
    public void createUserTest(){
        CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto();
        createUserRequestDto.setName("Test 1");
        createUserRequestDto.setAge(30);

        Response response =
            given()
                .contentType(ContentType.JSON)
                .body(createUserRequestDto)
            .when()
                .post(apiURL)
            .then()
                .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));

    }

    @Test
    @DisplayName("should return error when json is not valid")
    @Order(2)
    public void createUserValidationErrorTest(){
        CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto();
        createUserRequestDto.setAge(null);
        createUserRequestDto.setName(null);

        Response response =
            given()
                .contentType(ContentType.JSON)
                .body(createUserRequestDto)
            .when()
                .post(apiURL)
            .then()
                .extract().response();

        assertEquals(ResponseErrorDto.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));

    }

    @Test
    @DisplayName("should list all users")
    @Order(3)
    public void listAllUsersTest(){

        Response response =
            given()
                .contentType(ContentType.JSON)
            .when()
                .get(apiURL)
            .then()
                .extract().response();

        assertEquals(200, response.statusCode());
        assertNotNull(response.getBody());

    }

}

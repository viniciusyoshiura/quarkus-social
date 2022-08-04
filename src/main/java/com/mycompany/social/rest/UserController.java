package com.mycompany.social.rest;

import com.mycompany.social.domain.entity.UserEntity;
import com.mycompany.social.domain.repository.UserRepository;
import com.mycompany.social.rest.dto.CreateUserRequestDto;
import com.mycompany.social.rest.dto.ResponseErrorDto;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

    private UserRepository userRepository;
    private Validator validator;

    @Inject
    public UserController(UserRepository userRepository, Validator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequestDto createUserRequestDto) {

        Set<ConstraintViolation<CreateUserRequestDto>> violations = validator.validate(createUserRequestDto);

        if (!violations.isEmpty()) {

            return ResponseErrorDto
                    .createFromValidation(violations)
                    .withStatusCode(ResponseErrorDto.UNPROCESSABLE_ENTITY_STATUS);
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setAge(createUserRequestDto.getAge());
        userEntity.setName(createUserRequestDto.getName());
        userRepository.persist(userEntity);

        return Response
                .status(Response.Status.CREATED.getStatusCode())
                .entity(userEntity)
                .build();

    }

    @GET
    public Response listAllUsers() {

        PanacheQuery<UserEntity> allUsers = userRepository.findAll();

        return Response.ok(allUsers.list()).build();

    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        UserEntity user = userRepository.findById(id);
        if (user != null) {
            userRepository.delete(user);
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequestDto createUserRequestDto) {

        UserEntity user = userRepository.findById(id);
        if (user != null) {
            user.setName(createUserRequestDto.getName());
            user.setAge(createUserRequestDto.getAge());

            userRepository.persist(user);

            return Response.ok(user).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

}

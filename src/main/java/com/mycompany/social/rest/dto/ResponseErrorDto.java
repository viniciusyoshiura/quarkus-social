package com.mycompany.social.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ResponseErrorDto {

    public static final int UNPROCESSABLE_ENTITY_STATUS = 422;

    private String message;
    private Collection<FieldErrorDto> errors;


    public static <T> ResponseErrorDto createFromValidation(
            Set<ConstraintViolation<T>> violations){
        List<FieldErrorDto> errors = violations
                .stream()
                .map(cv -> new FieldErrorDto(cv.getPropertyPath().toString(), cv.getMessage()))
                .collect(Collectors.toList());

        String message = "Validation Error";

        ResponseErrorDto responseErrorDto = new ResponseErrorDto(message, errors);
        return responseErrorDto;
    }

    public Response withStatusCode(int code){
        return Response.status(code).entity(this).build();
    }

}

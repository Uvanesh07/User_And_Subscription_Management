package com.userms.exceptionhandler;


import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler{



    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error handleExceptions(ResourceNotFoundException exception, WebRequest webRequest) {
     String log = String.format("Exception : %s", String.join("\n",
                    Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toSet())));
        Error response = new Error();
            response.setCode(HttpStatus.NOT_FOUND.value());
            response.setDateTime(LocalDateTime.now());
            response.setHttpStatus(HttpStatus.NOT_FOUND);
            response.setMessage("The given id "+exception.getLocalizedMessage()+" is not found");
            response.setLog(log);
            response.setServiceErrorCode(exception.getServiceErrorCode());
        return response;
    }




    @ExceptionHandler(InternalServerError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Error handleExceptions(InternalServerError exception, WebRequest webRequest) {
        String log = String.format("Exception : %s", String.join("\n",
                Arrays.stream(exception.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.toSet())));
        Error response = new Error();
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setDateTime(LocalDateTime.now());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage("Something went wrong.Please contact admin.");
            response.setLog(log);
            response.setServiceErrorCode(exception.getServiceErrorCode());
        return response;
    }

    @ExceptionHandler(ServiceUnavalibleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Error ServiceUnavalibleException(InternalServerError exception, WebRequest webRequest) {
        String log = String.format("Exception : %s", String.join("\n",
                Arrays.stream(exception.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.toSet())));
        Error response = new Error();
        response.setCode(HttpStatus.FORBIDDEN.value());
        response.setDateTime(LocalDateTime.now());
        response.setHttpStatus(HttpStatus.FORBIDDEN);
        response.setMessage(exception.getMessage());
        response.setLog(log);
        response.setServiceErrorCode(exception.getServiceErrorCode());
        return response;
    }



    @ExceptionHandler(ProcessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error handleExceptions(ProcessException exception, WebRequest webRequest) {

        String log = String.format("Exception : %s", String.join("\n",
                Arrays.stream(exception.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.toSet())));
        Error response = new Error();
            response.setCode(HttpStatus.CONFLICT.value());
            response.setDateTime(LocalDateTime.now());
            response.setHttpStatus(HttpStatus.CONFLICT);
            response.setMessage(exception.getMessage());
            response.setLog(log);
            response.setServiceErrorCode(exception.getServiceErrorCode());
        return response;
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,})
    public Error handleValidationExceptions(
            Exception ex) {


        Error errorRes=new Error();
        Map<String, String> errors = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validationException = (MethodArgumentNotValidException) ex;
            validationException.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        } else if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex;
            constraintViolationException.getConstraintViolations().forEach(violation -> {
                String fieldName = violation.getPropertyPath().toString();
                String errorMessage = violation.getMessage();
                errors.put(fieldName, errorMessage);
            });
        }

        errorRes.setCode(HttpStatus.BAD_REQUEST.value());
        errorRes.setMessage("Request validation failed");
        errorRes.setValidation(errors);
        errorRes.setDateTime(LocalDateTime.now());
        errorRes.setHttpStatus(HttpStatus.BAD_REQUEST);
        return errorRes;
    }



    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class,})
    public Error badRequest(
            BadRequestException ex) {


        Error errorRes=new Error();
        errorRes.setCode(HttpStatus.BAD_REQUEST.value());
        errorRes.setMessage(ex.getMessage());
        errorRes.setDateTime(LocalDateTime.now());
        errorRes.setHttpStatus(HttpStatus.BAD_REQUEST);
        return errorRes;
    }

    @ExceptionHandler(DublicateExceptionHandler.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error dublicateExceptionHandler(DublicateExceptionHandler exception, WebRequest webRequest) {
        String log = String.format("Exception : %s", String.join("\n",
                Arrays.stream(exception.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.toSet())));
        Error response = new Error();
                response.setCode(HttpStatus.CONFLICT.value());
                response.setDateTime(LocalDateTime.now());
                response.setHttpStatus(HttpStatus.CONFLICT);
                if(Objects.nonNull(exception.getUserMessage()) && !exception.getUserMessage().isEmpty()){
                    response.setUserMessage(exception.getUserMessage());
                    response.setMessage(exception.getLocalizedMessage());
                }else{
                    response.setMessage("The given invoice  "+exception.getLocalizedMessage()+" is already in the system");
                }
               response.setServiceErrorCode(exception.getServiceErrorCode());
        return response;
    }

}

package org.bauerbrun0.snippetvault.api.advice;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.bauerbrun0.snippetvault.api.exception.*;
import org.bauerbrun0.snippetvault.api.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


@Slf4j
@RestControllerAdvice
public class ExceptionResolver {
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoHandlerFoundException(NoHandlerFoundException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException e) {
        return new ErrorResponse("Invalid credentials");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        if  (fieldError == null) {
            return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        String message = fieldError.getDefaultMessage();
        if  (message == null) {
            message = HttpStatus.BAD_REQUEST.getReasonPhrase();
        }
        return new ErrorResponse(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        return new ErrorResponse("User not found");
    }

    @ExceptionHandler(CannotDeleteLastAdminException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCannotDeleteLastAdminException(CannotDeleteLastAdminException e) {
        return new ErrorResponse("Cannot delete last admin user");
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDuplicateUsernameException(DuplicateUsernameException e) {
        return new ErrorResponse("Username already exists");
    }

    @ExceptionHandler(InvalidTagColorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidTagColorException(InvalidTagColorException e) {
        return new ErrorResponse("Invalid color string");
    }

    @ExceptionHandler(TagNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTagNotFoundException(TagNotFoundException e) {
        return new ErrorResponse("Tag not found");
    }

    @ExceptionHandler(DuplicateTagOnSnippetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDuplicateTagOnSnippetException(DuplicateTagOnSnippetException e) {
        return new ErrorResponse("Duplicate tag on snippet");
    }

    @ExceptionHandler(TagNotOnSnippetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTagNotOnSnippetException(TagNotOnSnippetException e) {
        return new ErrorResponse("Tag is not on snippet");
    }

    @ExceptionHandler(SnippetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleSnippetNotFoundException(SnippetNotFoundException e) {
        return new ErrorResponse("Snippet not found");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleExpiredToken(ExpiredJwtException e) {
        return new ErrorResponse("Token expired");
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        return new ErrorResponse("Unauthorized");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
}

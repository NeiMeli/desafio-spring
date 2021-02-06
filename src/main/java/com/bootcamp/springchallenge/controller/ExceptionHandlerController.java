package com.bootcamp.springchallenge.controller;

import com.bootcamp.springchallenge.controller.common.dto.ErrorDTO;
import com.bootcamp.springchallenge.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class ExceptionHandlerController {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleBadRequestException(Exception e) {
        final HttpStatus status;
        if (e instanceof BadRequestException) status = HttpStatus.BAD_REQUEST;
        else status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setStatus(status.value());
        errorDTO.setError(e.getClass().getSimpleName());
        errorDTO.setMessage(e.getMessage());
        return new ResponseEntity<>(errorDTO, status);
    }
}

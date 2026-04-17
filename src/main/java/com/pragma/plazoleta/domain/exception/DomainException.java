package com.pragma.plazoleta.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class DomainException extends RuntimeException{
    private final HttpStatus httpStatus;

    public DomainException(String message, HttpStatus httpStatus){
        super(message);
        this.httpStatus=httpStatus;
    }
}

package com.mycompany.jobhunter.util.error;

import com.mycompany.jobhunter.domain.dto.response.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class,
    })
    public ResponseEntity<RestResponse<Object>> handleBadCredentialsException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(ex.getMessage());
        res.setError("Bad credentials");
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            MissingCookiesException.class
    })
    public ResponseEntity<RestResponse<Object>> handleMissingCookies(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setMessage(ex.getMessage());
        res.setError("Missing required cookie");
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {
            DuplicatedKeyException.class
    })
    public ResponseEntity<RestResponse<Object>> hanldeDuplicatedKeyException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.ALREADY_REPORTED.value());
        res.setMessage(ex.getMessage());
        res.setError("Missing required cookie");
        return new ResponseEntity<>(res, HttpStatus.ALREADY_REPORTED);
    }

    @ExceptionHandler(value = {
            IdInvalidException.class
    })
    public ResponseEntity<RestResponse<Object>> hanldeIdInvalidException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(ex.getMessage());
        res.setError("Invalid id");
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }
}

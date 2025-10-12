package com.warehouse.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> responseBuilder(
            String message,
            HttpStatus httpStatus,
            Object responseObject
    ){
        Map<String, Object> response = Map.of(
                "message", message,
                "httpStatus", httpStatus,
                "data", responseObject
        );

        return new ResponseEntity<>(response, httpStatus);
    }
}

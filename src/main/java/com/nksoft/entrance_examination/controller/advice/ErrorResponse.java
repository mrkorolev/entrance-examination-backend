package com.nksoft.entrance_examination.controller.advice;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private final int statusCode;
    private final String message;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(HttpStatus status, String message) {
        this.statusCode = status.value();
        this.message = message;
    }
}

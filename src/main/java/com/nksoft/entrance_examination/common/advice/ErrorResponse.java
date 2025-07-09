package com.nksoft.entrance_examination.common.advice;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private final int statusCode;
    private final String message;
    private String file;
    private Integer line;
    private String method;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime timestamp = LocalDateTime.now();


    public ErrorResponse(HttpStatus status, String message, StackTraceElement origin) {
        this.statusCode = status.value();
        this.message = message;
        if (origin != null) {
            this.file = origin.getFileName();
            this.line = origin.getLineNumber();
            this.method = origin.getMethodName();
        }
    }
}

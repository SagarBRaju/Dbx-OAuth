package com.dropbox.oauth.exception;

import com.dropbox.oauth.dto.response.BaseResponse;
import com.dropbox.oauth.util.DbxErrorParser;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DbxOAuthException.class)
    public ResponseEntity<BaseResponse> handleCustomException(DbxOAuthException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(BaseResponse.error(DbxErrorParser.extractDbxError(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Unexpected error: " + ex.getMessage()));
    }
}


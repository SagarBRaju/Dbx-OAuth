package com.dropbox.oauth.exception;

public class DbxOAuthException extends RuntimeException {

    private final int statusCode;

    public DbxOAuthException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}


// for customising error response structure

package com.demo.project2.exception;

import java.util.Date;

public class ErrorMessage {

    private int statusCode;
    private Date timestamp;
    private String message;
    private String description;

    // constructor
    public ErrorMessage(int statusCode, Date timestamp, String message, String description) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.description = description;
    }

    // setters

    // getters
    public int getStatusCode() {
        return statusCode;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public String getMessage() {
        return message;
    }
    public String getDescription() {
        return description;
    }
}
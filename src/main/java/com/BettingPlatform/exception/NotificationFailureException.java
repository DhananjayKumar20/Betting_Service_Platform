package com.BettingPlatform.exception;

public class NotificationFailureException extends RuntimeException {
    public NotificationFailureException(String message) {
        super(message);
    }
}
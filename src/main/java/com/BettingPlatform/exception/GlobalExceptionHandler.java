package com.BettingPlatform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Status: FAIL, Message: " + ex.getMessage());
    }

	/*
	 * @ExceptionHandler(WalletServiceUnavailableException.class) public
	 * ResponseEntity<String>
	 * handleWalletServiceUnavailableException(WalletServiceUnavailableException ex)
	 * { return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).
	 * body("Status: ERROR, Message: " + ex.getMessage()); }
	 */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Status: FAIL, Message: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Status: ERROR, Message: An unexpected error occurred.");
    }
}

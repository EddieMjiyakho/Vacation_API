package com.eddie.vacation.exception;

public class InsufficientVacationDaysException extends RuntimeException {
    public InsufficientVacationDaysException(String message) {
        super(message);
    }
}
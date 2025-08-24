package com.eddie.vacation.exception;

public class VacationRequestNotFoundException extends RuntimeException {
    public VacationRequestNotFoundException(String message) {
        super(message);
    }
}
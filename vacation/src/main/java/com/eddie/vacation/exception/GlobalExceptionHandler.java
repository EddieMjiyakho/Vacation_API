package com.eddie.vacation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

   private ResponseEntity<Map<String, String>> errorResponse(HttpStatus status, String message) {
      return ResponseEntity.status(status).body(Map.of("message", message));
   }

   @ExceptionHandler(EmployeeNotFoundException.class)
   public ResponseEntity<Map<String, String>> handleEmployeeNotFound(EmployeeNotFoundException ex) {
      return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
   }

   @ExceptionHandler(VacationRequestNotFoundException.class)
   public ResponseEntity<Map<String, String>> handleRequestNotFound(VacationRequestNotFoundException ex) {
      return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
   }

   @ExceptionHandler(UnauthorizedException.class)
   public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException ex) {
      return errorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
   }

   @ExceptionHandler(InsufficientVacationDaysException.class)
   public ResponseEntity<Map<String, String>> handleInsufficientDays(InsufficientVacationDaysException ex) {
      return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
   }

   @ExceptionHandler(IllegalArgumentException.class)
   public ResponseEntity<Map<String, String>> handleIllegalArgs(IllegalArgumentException ex) {
      return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
   }

   @ExceptionHandler(Exception.class)
   public ResponseEntity<Map<String, String>> handleOtherExceptions(Exception ex) {
      return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
   }
}
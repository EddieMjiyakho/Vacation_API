package com.eddie.vacation.controller;

import com.eddie.vacation.dto.RequestStatusUpdateDto;
import com.eddie.vacation.exception.*;
import com.eddie.vacation.model.VacationRequest;
import com.eddie.vacation.service.VacationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController // combines @Controller + @ResponseBody (returns JSON/XML)
@RequestMapping("/api/manager") // maps http requests to controller methods
public class ManagerController {

    private final VacationService vacationService;

    @Autowired // constructor injection
    public ManagerController(VacationService vacationService) {
        this.vacationService = vacationService;
    }

    @GetMapping("/requests") // can fillter with status
    public ResponseEntity<?> getAllRequests(
            @RequestParam(required = false) String status) {
        try {
            List<VacationRequest> requests = (status == null)
                    ? vacationService.getAllRequests()
                    : vacationService.getRequestsByStatus(status.toLowerCase());
            return ResponseEntity.ok(requests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{managerId}/requests") // get all pending requests, manager only
    public ResponseEntity<?> getPendingRequests(@PathVariable Long managerId) {
        try {
            List<VacationRequest> pendingRequests = vacationService.getRequestsForManager(managerId);
            return ResponseEntity.ok(pendingRequests);
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/employee/{employeeId}/requests")
    public ResponseEntity<?> getEmployeeRequests(
            @PathVariable Long employeeId) {
        try {
            return ResponseEntity.ok(vacationService.getRequestsByEmployee(employeeId));
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/overlapping-requests") // get overlapping or clashing dates from this [start-end, dates]
    public ResponseEntity<?> findOverlappingRequests(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            return ResponseEntity.ok(vacationService.findOverlappingRequests(start, end));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid date format"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/request/{requestId}/status")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long requestId,
            @RequestBody RequestStatusUpdateDto statusUpdate) {
        try {
            VacationRequest updatedRequest = vacationService.updateRequestStatus(requestId, statusUpdate);
            return ResponseEntity.ok(updatedRequest);
        } catch (VacationRequestNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException | InsufficientVacationDaysException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred"));
        }
    }

    // wrapper for error messages always returns JSON object
    public static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
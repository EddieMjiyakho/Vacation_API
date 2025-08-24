package com.eddie.vacation.controller;

import com.eddie.vacation.dto.VacationRequestDto;
import com.eddie.vacation.exception.EmployeeNotFoundException;
import com.eddie.vacation.exception.InsufficientVacationDaysException;
import com.eddie.vacation.model.VacationRequest;
import com.eddie.vacation.service.VacationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final VacationService vacationService;

    @Autowired
    public EmployeeController(VacationService vacationService) {
        this.vacationService = vacationService;
    }

    @GetMapping("/{employeeId}/requests")
    public ResponseEntity<?> getEmployeeRequests(
            @PathVariable Long employeeId,
            @RequestParam(required = false) String status) {
        try {
            List<VacationRequest> requests = status == null
                    ? vacationService.getRequestsByEmployee(employeeId)
                    : vacationService.getRequestsByEmployeeAndStatus(employeeId, status);
            return ResponseEntity.ok(requests);
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{employeeId}/remaining-days")
    public ResponseEntity<?> getRemainingVacationDays(@PathVariable Long employeeId) {
        try {
            int remainingDays = vacationService.getRemainingVacationDays(employeeId);
            return ResponseEntity.ok(remainingDays);
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{employeeId}/requests")
    public ResponseEntity<?> createRequest(
            @PathVariable Long employeeId,
            @RequestBody VacationRequestDto requestDto) {
        try {
            requestDto.setAuthorId(employeeId);
            VacationRequest createdRequest = vacationService.createRequest(requestDto);
            return ResponseEntity.ok(createdRequest);
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        } catch (InsufficientVacationDaysException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Inner class for error responses
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
package com.eddie.vacation.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
public class VacationRequestDto {
    @NotNull(message = "Author ID is required")
    @Positive(message = "Author ID must be a positive number") // Validation annotations: enforce rules on incoming data
    private Long authorId;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDate vacationStartDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate vacationEndDate;

    @JsonIgnore
    public int getDurationInDays() {
        if (vacationStartDate == null || vacationEndDate == null) {
            throw new IllegalStateException("Both start and end dates must be set");
        }
        if (vacationStartDate.isAfter(vacationEndDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        return (int) ChronoUnit.DAYS.between(vacationStartDate, vacationEndDate) + 1;
    }

    public int validateAndGetDuration() {
        validateDates();
        return getDurationInDays();
    }

    public void validateDates() {
        if (vacationStartDate == null || vacationEndDate == null) {
            throw new IllegalArgumentException("Both start and end dates are required");
        }
        if (vacationStartDate.isAfter(vacationEndDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        if (vacationStartDate.isEqual(vacationEndDate)) {
            throw new IllegalArgumentException("Minimum vacation duration is 1 day");
        }
        if (vacationStartDate.isBefore(LocalDate.now().plusDays(1))) {
            throw new IllegalArgumentException("Start date must be at least tomorrow");
        }
    }

    public boolean overlapsWith(LocalDate otherStart, LocalDate otherEnd) {
        if (vacationStartDate == null || vacationEndDate == null || otherStart == null || otherEnd == null) {
            return false;
        }
        return !vacationEndDate.isBefore(otherStart) && !vacationStartDate.isAfter(otherEnd);
    }
}
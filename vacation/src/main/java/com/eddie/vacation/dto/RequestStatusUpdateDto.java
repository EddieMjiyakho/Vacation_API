package com.eddie.vacation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestStatusUpdateDto {
    @NotNull(message = "Manager ID is required")
    private Long managerId;

    @NotNull(message = "Status is required")
    @Pattern(regexp = "approved|rejected", message = "Status must be either 'approved' or 'rejected'")
    private String status;
}
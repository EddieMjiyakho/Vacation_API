package com.eddie.vacation.dto;

import lombok.Data;

@Data
public class EmployeeDto {
    private String name;
    private String email;
    private boolean isManager;
    private int remainingVacationDays = 30;
}

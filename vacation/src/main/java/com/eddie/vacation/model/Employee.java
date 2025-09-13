package com.eddie.vacation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "employees")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "is_manager", nullable = false)
    private boolean isManager = false;

    @Column(name = "remaining_vacation_days", nullable = false)
    private int remainingVacationDays = 30;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // EAGER always loads immediately
    @JsonIgnore
    private List<VacationRequest> vacationRequests;

    @OneToMany(mappedBy = "resolvedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<VacationRequest> resolvedRequests;
}
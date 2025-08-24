package com.eddie.vacation.service;

import com.eddie.vacation.dto.VacationRequestDto;
import com.eddie.vacation.exception.EmployeeNotFoundException;
import com.eddie.vacation.exception.InsufficientVacationDaysException;
import com.eddie.vacation.model.Employee;
import com.eddie.vacation.model.VacationRequest;
import com.eddie.vacation.repository.EmployeeRepository;
import com.eddie.vacation.repository.VacationRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VacationServiceTest {

    @Mock
    private VacationRequestRepository requestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private VacationService vacationService;

    private Employee employee;
    private VacationRequestDto requestDto;
    private VacationRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = new Employee();
        employee.setId(1L);
        employee.setRemainingVacationDays(10);

        requestDto = new VacationRequestDto();
        requestDto.setAuthorId(1L);
        requestDto.setVacationStartDate(LocalDate.now().plusDays(2));
        requestDto.setVacationEndDate(LocalDate.now().plusDays(5));

        request = new VacationRequest();
        request.setId(1L);
        request.setAuthor(employee);
        request.setVacationStartDate(requestDto.getVacationStartDate());
        request.setVacationEndDate(requestDto.getVacationEndDate());
    }

    @Test
    void createRequest_success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(requestRepository.findEmployeeOverlappingRequests(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(requestRepository.save(any(VacationRequest.class))).thenReturn(request);

        VacationRequest created = vacationService.createRequest(requestDto);

        assertNotNull(created);
        assertEquals(employee, created.getAuthor());
    }

    @Test
    void createRequest_employeeNotFound_throwsException() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());
        requestDto.setAuthorId(999L);

        assertThrows(EmployeeNotFoundException.class, () -> vacationService.createRequest(requestDto));
    }

    @Test
    void createRequest_insufficientDays_throwsException() {
        employee.setRemainingVacationDays(1); // less than requested
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThrows(InsufficientVacationDaysException.class, () -> vacationService.createRequest(requestDto));
    }

    @Test
    void createRequest_invalidDates_throwsException() {
        requestDto.setVacationStartDate(LocalDate.now().plusDays(5));
        requestDto.setVacationEndDate(LocalDate.now().plusDays(2));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThrows(IllegalArgumentException.class, () -> vacationService.createRequest(requestDto));
    }

    @Test
    void createRequest_overlappingRequests_throwsException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(requestRepository.findEmployeeOverlappingRequests(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(request)); // simulate overlap

        assertThrows(IllegalArgumentException.class, () -> vacationService.createRequest(requestDto));
    }
}
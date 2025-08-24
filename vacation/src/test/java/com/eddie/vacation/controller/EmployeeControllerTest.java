package com.eddie.vacation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.eddie.vacation.dto.VacationRequestDto;
import com.eddie.vacation.exception.EmployeeNotFoundException;
import com.eddie.vacation.exception.InsufficientVacationDaysException;
import com.eddie.vacation.model.Employee;
import com.eddie.vacation.model.VacationRequest;
import com.eddie.vacation.service.VacationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VacationService vacationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee;
    private VacationRequest vacationRequest;
    private VacationRequestDto validRequestDto;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setEmail("john@example.com");
        employee.setRemainingVacationDays(30);

        vacationRequest = new VacationRequest();
        vacationRequest.setId(1L);
        vacationRequest.setAuthor(employee);
        vacationRequest.setVacationStartDate(LocalDate.now().plusDays(1));
        vacationRequest.setVacationEndDate(LocalDate.now().plusDays(5));
        vacationRequest.setStatus("pending");

        validRequestDto = new VacationRequestDto();
        validRequestDto.setAuthorId(employee.getId());
        validRequestDto.setVacationStartDate(LocalDate.now().plusDays(1));
        validRequestDto.setVacationEndDate(LocalDate.now().plusDays(5));
    }

    @Test
    void getEmployeeRequests_success() throws Exception {
        Mockito.when(vacationService.getRequestsByEmployee(anyLong()))
                .thenReturn(List.of(vacationRequest));

        mockMvc.perform(get("/api/employee/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("pending"))
                .andExpect(jsonPath("$[0].author.id").value(1));
    }

    @Test
    void getEmployeeRequests_employeeNotFound_returns404() throws Exception {
        Mockito.when(vacationService.getRequestsByEmployee(anyLong()))
                .thenThrow(new EmployeeNotFoundException("Employee not found"));

        mockMvc.perform(get("/api/employee/999/requests"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }

    @Test
    void getRemainingVacationDays_success() throws Exception {
        Mockito.when(vacationService.getRemainingVacationDays(1L)).thenReturn(30);

        mockMvc.perform(get("/api/employee/1/remaining-days"))
                .andExpect(status().isOk())
                .andExpect(content().string("30"));
    }

    @Test
    void getRemainingVacationDays_employeeNotFound_returns404() throws Exception {
        Mockito.when(vacationService.getRemainingVacationDays(anyLong()))
                .thenThrow(new EmployeeNotFoundException("Employee not found"));

        mockMvc.perform(get("/api/employee/999/remaining-days"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }

    @Test
    void createRequest_success() throws Exception {
        Mockito.when(vacationService.createRequest(any(VacationRequestDto.class)))
                .thenReturn(vacationRequest);

        mockMvc.perform(post("/api/employee/1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("pending"))
                .andExpect(jsonPath("$.author.id").value(1));
    }

    @Test
    void createRequest_employeeNotFound_returns404() throws Exception {
        Mockito.when(vacationService.createRequest(any(VacationRequestDto.class)))
                .thenThrow(new EmployeeNotFoundException("Employee not found"));

        mockMvc.perform(post("/api/employee/999/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }

    @Test
    void createRequest_insufficientDays_returns400() throws Exception {
        Mockito.when(vacationService.createRequest(any(VacationRequestDto.class)))
                .thenThrow(new InsufficientVacationDaysException("Not enough vacation days remaining"));

        mockMvc.perform(post("/api/employee/1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Not enough vacation days remaining"));
    }

    @Test
    void createRequest_invalidDates_returns400() throws Exception {
        VacationRequestDto invalidDto = new VacationRequestDto();
        invalidDto.setAuthorId(1L);
        invalidDto.setVacationStartDate(LocalDate.now().plusDays(5));
        invalidDto.setVacationEndDate(LocalDate.now().plusDays(1));

        Mockito.when(vacationService.createRequest(any(VacationRequestDto.class)))
                .thenThrow(new IllegalArgumentException("End date must be after start date"));

        mockMvc.perform(post("/api/employee/1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("End date must be after start date"));
    }

    @Test
    void getEmployeeRequests_withStatusFilter() throws Exception {
        Mockito.when(vacationService.getRequestsByEmployeeAndStatus(1L, "pending"))
                .thenReturn(List.of(vacationRequest));

        mockMvc.perform(get("/api/employee/1/requests")
                .param("status", "pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("pending"));
    }
}
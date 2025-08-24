package com.eddie.vacation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.eddie.vacation.dto.RequestStatusUpdateDto;
import com.eddie.vacation.exception.*;
import com.eddie.vacation.model.Employee;
import com.eddie.vacation.model.VacationRequest;
import com.eddie.vacation.repository.EmployeeRepository;
import com.eddie.vacation.service.VacationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManagerController.class)
public class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VacationService vacationService;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private VacationRequest vacationRequest;
    private Employee manager;

    @BeforeEach
    void setUp() {
        vacationRequest = new VacationRequest();
        vacationRequest.setId(1L);
        vacationRequest.setVacationStartDate(LocalDate.now().plusDays(1));
        vacationRequest.setVacationEndDate(LocalDate.now().plusDays(5));
        vacationRequest.setStatus("pending");

        manager = new Employee();
        manager.setId(2L);
        manager.setName("Manager Name");
        manager.setManager(true);
    }

    @Test
    void getManagerRequests_success() throws Exception {
        when(vacationService.getRequestsByStatus("pending"))
                .thenReturn(List.of(vacationRequest));

        mockMvc.perform(get("/api/manager/requests?status=pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("pending"));
    }

    @Test
    void getPendingRequests_success() throws Exception {
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(vacationService.getRequestsForManager(2L))
                .thenReturn(List.of(vacationRequest));

        mockMvc.perform(get("/api/manager/2/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("pending"));
    }

    @Test
    void getPendingRequests_notManager_returns403() throws Exception {
        Employee nonManager = new Employee();
        nonManager.setId(3L);
        nonManager.setManager(false);

        when(employeeRepository.findById(3L)).thenReturn(Optional.of(nonManager));
        when(vacationService.getRequestsForManager(3L))
                .thenThrow(new UnauthorizedException("Only managers can view requests"));

        mockMvc.perform(get("/api/manager/3/requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateRequestStatus_success() throws Exception {
        RequestStatusUpdateDto dto = new RequestStatusUpdateDto();
        dto.setStatus("approved");
        dto.setManagerId(2L);

        VacationRequest approvedRequest = new VacationRequest();
        approvedRequest.setId(1L);
        approvedRequest.setStatus("approved");
        approvedRequest.setVacationStartDate(LocalDate.now().plusDays(1));
        approvedRequest.setVacationEndDate(LocalDate.now().plusDays(5));

        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(vacationService.updateRequestStatus(anyLong(), any(RequestStatusUpdateDto.class)))
                .thenReturn(approvedRequest);

        mockMvc.perform(put("/api/manager/request/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("approved"));
    }

    @Test
    void updateRequestStatus_requestNotFound_returns404() throws Exception {
        RequestStatusUpdateDto dto = new RequestStatusUpdateDto();
        dto.setStatus("approved");
        dto.setManagerId(2L);

        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(vacationService.updateRequestStatus(anyLong(), any(RequestStatusUpdateDto.class)))
                .thenThrow(new VacationRequestNotFoundException("Request not found"));

        mockMvc.perform(put("/api/manager/request/999/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Request not found"));
    }

    @Test
    void updateRequestStatus_unauthorized_returns403() throws Exception {
        RequestStatusUpdateDto dto = new RequestStatusUpdateDto();
        dto.setStatus("approved");
        dto.setManagerId(3L); // Non-manager ID

        Employee nonManager = new Employee();
        nonManager.setId(3L);
        nonManager.setManager(false);

        when(employeeRepository.findById(3L)).thenReturn(Optional.of(nonManager));
        when(vacationService.updateRequestStatus(anyLong(), any(RequestStatusUpdateDto.class)))
                .thenThrow(new UnauthorizedException("Only managers can approve requests"));

        mockMvc.perform(put("/api/manager/request/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Only managers can approve requests"));
    }

    @Test
    void getEmployeeRequests_success() throws Exception {
        when(vacationService.getRequestsByEmployee(1L))
                .thenReturn(List.of(vacationRequest));

        mockMvc.perform(get("/api/manager/employee/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void findOverlappingRequests_success() throws Exception {
        when(vacationService.findOverlappingRequests(any(), any()))
                .thenReturn(List.of(vacationRequest));

        mockMvc.perform(get("/api/manager/overlapping-requests")
                .param("startDate", "2023-12-01")
                .param("endDate", "2023-12-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
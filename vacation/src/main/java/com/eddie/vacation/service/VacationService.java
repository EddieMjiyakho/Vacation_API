package com.eddie.vacation.service;

import com.eddie.vacation.dto.RequestStatusUpdateDto;
import com.eddie.vacation.dto.VacationRequestDto;
import com.eddie.vacation.exception.*;
import com.eddie.vacation.model.Employee;
import com.eddie.vacation.model.VacationRequest;
import com.eddie.vacation.repository.EmployeeRepository;
import com.eddie.vacation.repository.VacationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class VacationService {

    private final VacationRequestRepository requestRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public VacationService(VacationRequestRepository requestRepository,
            EmployeeRepository employeeRepository) {
        this.requestRepository = requestRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public VacationRequest createRequest(VacationRequestDto requestDto) {
        requestDto.validateDates();

        Employee employee = employeeRepository.findById(requestDto.getAuthorId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        int requestedDays = requestDto.getDurationInDays();
        if (employee.getRemainingVacationDays() < requestedDays) {
            throw new InsufficientVacationDaysException("Not enough vacation days remaining");
        }

        List<VacationRequest> overlaps = requestRepository.findEmployeeOverlappingRequests(
                employee.getId(),
                requestDto.getVacationStartDate(),
                requestDto.getVacationEndDate());
        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException("Vacation dates overlap with an existing request");
        }

        VacationRequest request = new VacationRequest();
        request.setAuthor(employee);
        request.setVacationStartDate(requestDto.getVacationStartDate());
        request.setVacationEndDate(requestDto.getVacationEndDate());
        request.setStatus("pending");

        return requestRepository.save(request);
    }

    @Transactional
    public VacationRequest updateRequestStatus(Long requestId, RequestStatusUpdateDto statusUpdate) {
        if (!"approved".equals(statusUpdate.getStatus()) && !"rejected".equals(statusUpdate.getStatus())) {
            throw new IllegalArgumentException("Status must be either 'approved' or 'rejected'");
        }

        VacationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new VacationRequestNotFoundException("Request not found"));

        Employee manager = employeeRepository.findById(statusUpdate.getManagerId())
                .orElseThrow(() -> new EmployeeNotFoundException("Manager not found"));

        if (!manager.isManager()) {
            throw new UnauthorizedException("Only managers can approve/reject requests");
        }

        if ("approved".equals(statusUpdate.getStatus())) {
            if (!"pending".equals(request.getStatus())) {
                throw new IllegalArgumentException("Only pending requests can be approved");
            }

            Employee employee = request.getAuthor();
            int duration = request.getDurationInDays();
            if (employee.getRemainingVacationDays() < duration) {
                throw new InsufficientVacationDaysException("Not enough remaining vacation days");
            }

            employee.setRemainingVacationDays(employee.getRemainingVacationDays() - duration);
            employeeRepository.save(employee);
        }

        request.setStatus(statusUpdate.getStatus());
        request.setResolvedBy(manager);
        return requestRepository.save(request);
    }

    public List<VacationRequest> getRequestsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        return requestRepository.findByAuthor(employee);
    }

    public List<VacationRequest> getRequestsByEmployeeAndStatus(Long employeeId, String status) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        return requestRepository.findByAuthorAndStatus(employee, status);
    }

    public List<VacationRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    public List<VacationRequest> getRequestsByStatus(String status) {
        return requestRepository.findByStatus(status);
    }

    public int getRemainingVacationDays(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        return employee.getRemainingVacationDays();
    }

    public List<VacationRequest> findOverlappingRequests(LocalDate startDate, LocalDate endDate) {
        return requestRepository.findOverlappingApprovedRequests(startDate, endDate);
    }

    public List<VacationRequest> getPendingRequestsForManager() {
        return requestRepository.findAllPendingRequests();
    }

    public List<VacationRequest> getRequestsForManager(Long managerId) {
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new EmployeeNotFoundException("Manager not found"));

        if (!manager.isManager()) {
            throw new UnauthorizedException("Only managers can view requests");
        }

        return requestRepository.findAllPendingRequests();
    }
}

package com.eddie.vacation.repository;

import com.eddie.vacation.model.VacationRequest;
import com.eddie.vacation.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository //marking data access objects in this class as bean
public interface VacationRequestRepository extends JpaRepository<VacationRequest, Long> {

   List<VacationRequest> findByAuthor(Employee employee);

   List<VacationRequest> findByAuthorAndStatus(Employee employee, String status);

   List<VacationRequest> findByStatus(String status);

   List<VacationRequest> findByResolvedBy(Employee manager);

   @Query("SELECT vr FROM VacationRequest vr " +
                   "WHERE vr.status = 'approved' " +
                   "AND vr.vacationStartDate <= :endDate " +
                   "AND vr.vacationEndDate >= :startDate")
   List<VacationRequest> findOverlappingApprovedRequests(
                   @Param("startDate") LocalDate startDate,
                   @Param("endDate") LocalDate endDate);

   @Query("SELECT vr FROM VacationRequest vr " +
                   "WHERE vr.author.id = :employeeId " +
                   "AND vr.vacationStartDate <= :endDate " +
                   "AND vr.vacationEndDate >= :startDate")
   List<VacationRequest> findEmployeeOverlappingRequests(
                   @Param("employeeId") Long employeeId,
                   @Param("startDate") LocalDate startDate,
                   @Param("endDate") LocalDate endDate);

   @Query("SELECT vr FROM VacationRequest vr " +
                   "WHERE vr.status = 'pending'") // For managers to see all requests pending approval
   List<VacationRequest> findAllPendingRequests();
}

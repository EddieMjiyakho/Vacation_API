package com.eddie.vacation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Entity
@Table(name = "vacation_requests")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" }) // clean JSON (avoid recursion & Hibernate noise)
public class VacationRequest {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "author_id", nullable = false) // foreign key column
   @JsonIgnoreProperties({ "vacationRequests", "resolvedRequests", "hibernateLazyInitializer", "handler" })
   private Employee author;

   @Column(nullable = false)
   private String status = "pending"; // approved, rejected, pending

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "resolved_by_id") // foreign key column
   @JsonIgnoreProperties({ "vacationRequests", "resolvedRequests", "hibernateLazyInitializer", "handler" })
   private Employee resolvedBy;

   @Column(name = "request_created_at", nullable = false)
   private LocalDateTime requestCreatedAt = LocalDateTime.now();

   @Column(name = "vacation_start_date", nullable = false)
   private LocalDate vacationStartDate;

   @Column(name = "vacation_end_date", nullable = false)
   private LocalDate vacationEndDate;

   @Transient // not persisted in DB, calcs at runtime, no column is created in the table
   public int getDurationInDays() {
      return (int) ChronoUnit.DAYS.between(vacationStartDate, vacationEndDate) + 1;
   }
}

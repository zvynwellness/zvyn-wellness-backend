package com.homycarev1.ecommerce.dao;

import com.homycarev1.ecommerce.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.time.LocalDateTime;

@RepositoryRestResource(exported = false)
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByServiceId(Long serviceId);
    List<Reservation> findByCustomerEmailIgnoreCaseOrderByPreferredDateTimeDesc(String email);
    List<Reservation> findByEmployeeUserEmailIgnoreCaseOrderByPreferredDateTimeAsc(String email);
    List<Reservation> findByEmployeeIsNullAndStatusOrderByPreferredDateTimeAsc(com.homycarev1.ecommerce.entity.ReservationStatus status);
    long countByCustomerIdAndPreferredDateTimeGreaterThanEqualAndPreferredDateTimeLessThanAndStatusNot(
            Long customerId, LocalDateTime from, LocalDateTime to, com.homycarev1.ecommerce.entity.ReservationStatus status);
    long countByServiceIdAndCustomerRoleAndPreferredDateTimeGreaterThanEqualAndPreferredDateTimeLessThanAndStatusNot(
            Long serviceId, com.homycarev1.ecommerce.entity.UserRole role, LocalDateTime from, LocalDateTime to,
            com.homycarev1.ecommerce.entity.ReservationStatus status);
}

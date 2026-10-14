package com.homycarev1.ecommerce.controller;

import com.homycarev1.ecommerce.dao.AppUserRepository;
import com.homycarev1.ecommerce.dao.ReservationRepository;
import com.homycarev1.ecommerce.dao.WellnessServiceRepository;
import com.homycarev1.ecommerce.entity.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationRepository reservations;
    private final AppUserRepository users;
    private final WellnessServiceRepository services;

    public ReservationController(ReservationRepository reservations, AppUserRepository users, WellnessServiceRepository services) {
        this.reservations = reservations;
        this.users = users;
        this.services = services;
    }

    @GetMapping("/mine")
    public List<ReservationView> mine(Authentication authentication) {
        return reservations.findByCustomerEmailIgnoreCaseOrderByPreferredDateTimeDesc(authentication.getName())
                .stream().map(ReservationView::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationView create(@Valid @RequestBody CreateReservationRequest request, Authentication authentication) {
        AppUser customer = users.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (customer.getRole() == UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin accounts cannot create personal reservations");
        }
        if (customer.getRole() == UserRole.EMPLOYEE) {
            YearMonth month = YearMonth.from(request.preferredDateTime());
            long monthlyReservations = reservations.countByCustomerIdAndPreferredDateTimeGreaterThanEqualAndPreferredDateTimeLessThanAndStatusNot(
                    customer.getId(), month.atDay(1).atStartOfDay(), month.plusMonths(1).atDay(1).atStartOfDay(), ReservationStatus.CANCELLED);
            if (monthlyReservations >= 5) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Employees may reserve a maximum of five sessions per month");
            }
        }
        WellnessService service = services.findById(request.serviceId())
                .filter(WellnessService::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wellness service not found"));
        YearMonth reservationMonth = YearMonth.from(request.preferredDateTime());
        int capacity = customer.getRole() == UserRole.EMPLOYEE ? service.getEmployeeCapacity() : service.getCustomerCapacity();
        long reservedSlots = reservations.countByServiceIdAndCustomerRoleAndPreferredDateTimeGreaterThanEqualAndPreferredDateTimeLessThanAndStatusNot(
                service.getId(), customer.getRole(), reservationMonth.atDay(1).atStartOfDay(),
                reservationMonth.plusMonths(1).atDay(1).atStartOfDay(), ReservationStatus.CANCELLED);
        if (reservedSlots >= capacity) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No remaining slots for this account type in the selected month");
        }

        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setService(service);
        reservation.setPreferredDateTime(request.preferredDateTime());
        reservation.setNotes(request.notes());
        reservation.setStatus(ReservationStatus.PENDING);
        return ReservationView.from(reservations.save(reservation));
    }

    @PatchMapping("/{id}/cancel")
    public ReservationView cancel(@PathVariable Long id, Authentication authentication) {
        Reservation reservation = reservations.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!reservation.getCustomer().getEmail().equalsIgnoreCase(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return ReservationView.from(reservation);
        }
        if (LocalDateTime.now().plusHours(24).isAfter(reservation.getPreferredDateTime())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Appointments within 24 hours must be cancelled through customer service");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        return ReservationView.from(reservations.save(reservation));
    }

    public record CreateReservationRequest(
            @NotNull Long serviceId,
            @NotNull @Future LocalDateTime preferredDateTime,
            @Size(max = 1000) String notes) {
    }

    public record ReservationView(Long id, Long serviceId, String serviceName, LocalDateTime preferredDateTime,
                                  String notes, ReservationStatus status, String instructorName) {
        static ReservationView from(Reservation reservation) {
            return new ReservationView(reservation.getId(), reservation.getService().getId(),
                    reservation.getService().getName(), reservation.getPreferredDateTime(),
                    reservation.getNotes(), reservation.getStatus(),
                    reservation.getEmployee() == null ? null : reservation.getEmployee().getUser().getFullName());
        }
    }
}

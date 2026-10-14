package com.homycarev1.ecommerce.controller;

import com.homycarev1.ecommerce.dao.EmployeeProfileRepository;
import com.homycarev1.ecommerce.dao.ReservationRepository;
import com.homycarev1.ecommerce.dao.WellnessServiceRepository;
import com.homycarev1.ecommerce.entity.EmployeeProfile;
import com.homycarev1.ecommerce.entity.Reservation;
import com.homycarev1.ecommerce.entity.ReservationStatus;
import com.homycarev1.ecommerce.entity.WellnessService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeDashboardController {
    private final EmployeeProfileRepository employees;
    private final ReservationRepository reservations;
    private final WellnessServiceRepository services;

    public EmployeeDashboardController(EmployeeProfileRepository employees, ReservationRepository reservations,
                                       WellnessServiceRepository services) {
        this.employees = employees;
        this.reservations = reservations;
        this.services = services;
    }

    @GetMapping("/dashboard")
    public DashboardView dashboard(Authentication authentication) {
        EmployeeProfile employee = employees.findByUserEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee profile not found"));
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        List<Reservation> assigned = reservations.findByEmployeeUserEmailIgnoreCaseOrderByPreferredDateTimeAsc(authentication.getName());
        List<Reservation> newRequests = reservations.findByEmployeeIsNullAndStatusOrderByPreferredDateTimeAsc(ReservationStatus.PENDING);

        List<SessionView> todaySessions = assigned.stream()
                .filter(item -> item.getPreferredDateTime().toLocalDate().equals(today))
                .filter(item -> item.getStatus() != ReservationStatus.CANCELLED)
                .map(SessionView::from).toList();
        List<SessionView> upcoming = assigned.stream()
                .filter(item -> item.getPreferredDateTime().isAfter(now))
                .filter(item -> item.getStatus() != ReservationStatus.CANCELLED)
                .map(SessionView::from).toList();
        List<SessionView> cancellations = assigned.stream()
                .filter(item -> item.getStatus() == ReservationStatus.CANCELLED)
                .map(SessionView::from).toList();
        List<SessionView> pending = assigned.stream()
                .filter(item -> item.getStatus() == ReservationStatus.PENDING)
                .map(SessionView::from).toList();

        long assignedClients = assigned.stream().map(item -> item.getCustomer().getId()).distinct().count();
        long activeWorkload = assigned.stream()
                .filter(item -> item.getStatus() != ReservationStatus.CANCELLED && item.getStatus() != ReservationStatus.COMPLETED)
                .count();
        List<String> notifications = new ArrayList<>();
        if (!newRequests.isEmpty()) notifications.add(newRequests.size() + " new reservation request(s) need review.");
        if (!todaySessions.isEmpty()) notifications.add(todaySessions.size() + " session(s) are scheduled today.");
        if (!cancellations.isEmpty()) notifications.add(cancellations.size() + " cancellation(s) are recorded.");
        if (notifications.isEmpty()) notifications.add("You are all caught up.");

        return new DashboardView(employee.getUser().getFullName(), employee.getJobTitle(),
                todaySessions, upcoming, newRequests.stream().map(SessionView::from).toList(),
                cancellations, pending, notifications, activeWorkload, assignedClients,
                services.findByInstructorIdAndActiveTrueOrderByScheduledDateTimeAsc(employee.getId())
                        .stream().map(ServiceLocationView::from).toList());
    }

    @PatchMapping("/services/{id}/location")
    public ServiceLocationView updateLocation(@PathVariable Long id, @Valid @RequestBody LocationRequest request,
                                              Authentication authentication) {
        EmployeeProfile employee = employees.findByUserEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee profile not found"));
        WellnessService service = services.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
        if (service.getInstructor() == null || !service.getInstructor().getId().equals(employee.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit locations for services assigned to you");
        }
        service.setLocation(request.location().trim());
        service.setGoogleMapsUrl(request.googleMapsUrl() == null || request.googleMapsUrl().isBlank() ? null : request.googleMapsUrl().trim());
        return ServiceLocationView.from(services.save(service));
    }

    public record DashboardView(String employeeName, String jobTitle, List<SessionView> todaySessions,
                                List<SessionView> upcomingAppointments, List<SessionView> newReservationRequests,
                                List<SessionView> cancellations, List<SessionView> pendingActions,
                                List<String> notifications, long activeWorkload, long assignedClients,
                                List<ServiceLocationView> assignedServices) { }

    public record LocationRequest(@NotBlank @Size(max=255) String location,
                                  @Size(max=1000) @Pattern(regexp="^$|^https://.*", message="Google Maps URL must start with https://") String googleMapsUrl) { }

    public record ServiceLocationView(Long id, String name, String location, String googleMapsUrl, LocalDateTime scheduledDateTime) {
        static ServiceLocationView from(WellnessService service) {
            return new ServiceLocationView(service.getId(), service.getName(), service.getLocation(),
                    service.getGoogleMapsUrl(), service.getScheduledDateTime());
        }
    }

    public record SessionView(Long id, String customerName, String serviceName, LocalDateTime preferredDateTime,
                              ReservationStatus status, String notes) {
        static SessionView from(Reservation reservation) {
            return new SessionView(reservation.getId(), reservation.getCustomer().getFullName(),
                    reservation.getService().getName(), reservation.getPreferredDateTime(),
                    reservation.getStatus(), reservation.getNotes());
        }
    }
}

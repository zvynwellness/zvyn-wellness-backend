package com.homycarev1.ecommerce.controller;

import com.homycarev1.ecommerce.dao.*;
import com.homycarev1.ecommerce.entity.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {
    private final ReservationRepository reservations;
    private final WellnessServiceRepository services;
    private final EmployeeProfileRepository employees;
    private final MonthlyServiceRepository monthlyServices;

    public AdminDashboardController(ReservationRepository reservations, WellnessServiceRepository services,
                                    EmployeeProfileRepository employees, MonthlyServiceRepository monthlyServices) {
        this.reservations = reservations;
        this.services = services;
        this.employees = employees;
        this.monthlyServices = monthlyServices;
    }

    @GetMapping("/dashboard")
    public DashboardView dashboard() {
        return new DashboardView(
                reservations.findByEmployeeIsNullAndStatusOrderByPreferredDateTimeAsc(ReservationStatus.PENDING)
                        .stream().map(ReservationAdminView::from).toList(),
                services.findAll().stream().filter(WellnessService::isActive).map(ServiceView::from).toList(),
                employees.findAll().stream().map(EmployeeView::from).toList(),
                monthlyServices.findAllByOrderByServiceMonthAsc().stream().map(MonthlyServiceView::from).toList());
    }

    @PatchMapping("/reservations/{id}/confirm")
    public ReservationAdminView confirm(@PathVariable Long id, @Valid @RequestBody ConfirmRequest request) {
        Reservation reservation = reservations.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
        EmployeeProfile employee = employees.findById(request.employeeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        reservation.setEmployee(employee);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        return ReservationAdminView.from(reservations.save(reservation));
    }

    @PostMapping("/services")
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceView createService(@Valid @RequestBody ServiceRequest request) {
        WellnessService service = new WellnessService();
        applyService(service, request);
        return ServiceView.from(services.save(service));
    }

    @PutMapping("/services/{id}")
    public ServiceView updateService(@PathVariable Long id, @Valid @RequestBody ServiceRequest request) {
        WellnessService service = services.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        applyService(service, request);
        return ServiceView.from(services.save(service));
    }

    @DeleteMapping("/services/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteService(@PathVariable Long id) {
        WellnessService service = services.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        monthlyServices.deleteByServiceId(id);
        if (reservations.existsByServiceId(id)) {
            service.setActive(false);
            services.save(service);
        } else {
            services.delete(service);
        }
    }

    @PostMapping("/monthly-services")
    @ResponseStatus(HttpStatus.CREATED)
    public MonthlyServiceView createMonthlyService(@Valid @RequestBody MonthlyServiceRequest request) {
        if (monthlyServices.countByActiveTrue() >= 3) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only three services can be displayed on the homepage. Remove one before adding another.");
        }
        if (monthlyServices.existsByServiceId(request.serviceId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This service is already displayed on the homepage.");
        }
        MonthlyService monthly = new MonthlyService();
        applyMonthlyService(monthly, request);
        return MonthlyServiceView.from(monthlyServices.save(monthly));
    }

    @PutMapping("/monthly-services/{id}")
    public MonthlyServiceView updateMonthlyService(@PathVariable Long id, @Valid @RequestBody MonthlyServiceRequest request) {
        MonthlyService monthly = monthlyServices.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        applyMonthlyService(monthly, request);
        return MonthlyServiceView.from(monthlyServices.save(monthly));
    }

    @DeleteMapping("/monthly-services/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMonthlyService(@PathVariable Long id) {
        monthlyServices.deleteById(id);
    }

    private void applyService(WellnessService service, ServiceRequest request) {
        service.setName(request.name().trim());
        service.setSlug(request.slug().trim().toLowerCase());
        service.setDescription(request.description().trim());
        service.setCategory(request.category().trim().toUpperCase());
        service.setDurationMinutes(request.durationMinutes());
        service.setImageUrl(request.imageUrl());
        service.setDisplayOrder(request.displayOrder());
        service.setActive(request.active());
        service.setCustomerCapacity(request.customerCapacity());
        service.setEmployeeCapacity(request.employeeCapacity());
        service.setLocation(request.location().trim());
        service.setGoogleMapsUrl(request.googleMapsUrl() == null || request.googleMapsUrl().isBlank() ? null : request.googleMapsUrl().trim());
        service.setPriceAmount(request.priceAmount());
        service.setCurrency(request.currency().trim().toUpperCase());
        service.setScheduledDateTime(request.scheduledDateTime());
        service.setInstructor(request.instructorEmployeeId() == null ? null : employees.findById(request.instructorEmployeeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found")));
    }

    private void applyMonthlyService(MonthlyService monthly, MonthlyServiceRequest request) {
        WellnessService service = services.findById(request.serviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
        monthly.setService(service);
        monthly.setEmployee(service.getInstructor());
        monthly.setServiceMonth(LocalDate.now().withDayOfMonth(1));
        monthly.setActive(true);
    }

    public record DashboardView(List<ReservationAdminView> pendingReservations, List<ServiceView> services,
                                List<EmployeeView> employees, List<MonthlyServiceView> monthlyServices) { }
    public record ConfirmRequest(@NotNull Long employeeId) { }
    public record ServiceRequest(@NotBlank @Size(max=120) String name, @NotBlank @Size(max=140) String slug,
                                 @NotBlank @Size(max=1600) String description, @NotBlank @Pattern(regexp="MOVE|REFLECT|RESTORE_EXPLORE", message="Category must be Move, Reflect, or Restore & Explore") String category, @Min(15) @Max(480) int durationMinutes,
                                 String imageUrl, int displayOrder, boolean active,
                                 @Min(0) int customerCapacity, @Min(0) int employeeCapacity,
                                 @NotBlank @Size(max=255) String location,
                                 @Size(max=1000) @Pattern(regexp="^$|^https://.*", message="Google Maps URL must start with https://") String googleMapsUrl,
                                 @DecimalMin("0.0") BigDecimal priceAmount,
                                 @NotBlank @Size(min=3, max=3) String currency,
                                 @NotNull LocalDateTime scheduledDateTime, Long instructorEmployeeId) { }
    public record MonthlyServiceRequest(@NotNull Long serviceId) { }
    public record ServiceView(Long id, String name, String slug, String description, String category, int durationMinutes,
                              String imageUrl, int displayOrder, boolean active, int customerCapacity,
                              int employeeCapacity, String location, String googleMapsUrl, BigDecimal priceAmount, String currency,
                              LocalDateTime scheduledDateTime, Long instructorEmployeeId, String instructorName) {
        static ServiceView from(WellnessService value) { return new ServiceView(value.getId(), value.getName(), value.getSlug(), value.getDescription(), value.getCategory(), value.getDurationMinutes(), value.getImageUrl(), value.getDisplayOrder(), value.isActive(), value.getCustomerCapacity(), value.getEmployeeCapacity(), value.getLocation(), value.getGoogleMapsUrl(), value.getPriceAmount(), value.getCurrency(), value.getScheduledDateTime(), value.getInstructor() == null ? null : value.getInstructor().getId(), value.getInstructor() == null ? null : value.getInstructor().getUser().getFullName()); }
    }
    public record EmployeeView(Long id, String name, String jobTitle, String specialty) {
        static EmployeeView from(EmployeeProfile value) { return new EmployeeView(value.getId(), value.getUser().getFullName(), value.getJobTitle(), value.getSpecialty()); }
    }
    public record ReservationAdminView(Long id, String customerName, String serviceName, java.time.LocalDateTime preferredDateTime,
                                       ReservationStatus status, String instructorName) {
        static ReservationAdminView from(Reservation value) { return new ReservationAdminView(value.getId(), value.getCustomer().getFullName(), value.getService().getName(), value.getPreferredDateTime(), value.getStatus(), value.getEmployee() == null ? null : value.getEmployee().getUser().getFullName()); }
    }
    public record MonthlyServiceView(Long id, Long serviceId, String serviceName, Long employeeId, String instructorName, LocalDate serviceMonth) {
        static MonthlyServiceView from(MonthlyService value) { var instructor = value.getService().getInstructor(); return new MonthlyServiceView(value.getId(), value.getService().getId(), value.getService().getName(), instructor == null ? null : instructor.getId(), instructor == null ? null : instructor.getUser().getFullName(), value.getServiceMonth()); }
    }
}

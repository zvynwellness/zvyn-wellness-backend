package com.homycarev1.ecommerce.controller;

import com.homycarev1.ecommerce.dao.MonthlyServiceRepository;
import com.homycarev1.ecommerce.entity.MonthlyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/monthly-services")
public class MonthlyServiceController {
    private final MonthlyServiceRepository monthlyServices;
    public MonthlyServiceController(MonthlyServiceRepository monthlyServices) { this.monthlyServices = monthlyServices; }

    @GetMapping
    public List<MonthlyServiceView> activeMonthlyServices() {
        return monthlyServices.findAllByOrderByServiceMonthAsc().stream()
                .filter(MonthlyService::isActive)
                .filter(item -> item.getService().isActive())
                .limit(3)
                .map(MonthlyServiceView::from).toList();
    }

    public record MonthlyServiceView(Long id, Long serviceId, String serviceName, String description,
                                     int durationMinutes, LocalDate serviceMonth, String instructorName,
                                     int customerCapacity, int employeeCapacity, String location,
                                     String googleMapsUrl, BigDecimal priceAmount, String currency, LocalDateTime scheduledDateTime) {
        static MonthlyServiceView from(MonthlyService value) {
            var service = value.getService();
            return new MonthlyServiceView(value.getId(), service.getId(), service.getName(), service.getDescription(),
                    service.getDurationMinutes(), value.getServiceMonth(), service.getInstructor() == null ? null : service.getInstructor().getUser().getFullName(),
                    service.getCustomerCapacity(), service.getEmployeeCapacity(), service.getLocation(), service.getGoogleMapsUrl(),
                    service.getPriceAmount(), service.getCurrency(), service.getScheduledDateTime());
        }
    }
}

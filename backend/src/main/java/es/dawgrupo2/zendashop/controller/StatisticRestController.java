package es.dawgrupo2.zendashop.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.dawgrupo2.zendashop.model.User;
import es.dawgrupo2.zendashop.service.OrderService;
import es.dawgrupo2.zendashop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticRestController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/income")
    public Map<String, Object> getIncomeStatistics(
            @RequestParam String period,
            @RequestParam int number) {

        String validatedPeriod = validate(period, number);
        List<Double> data;

        switch (validatedPeriod) {
            case "day": data = orderService.getDailyIncomeLastDays(number); break;
            case "month": data = orderService.getMonthlyIncomeLastMonths(number); break;
            case "year": data = orderService.getYearlyIncomeLastYears(number); break;
            default: throw new IllegalArgumentException("El periodo debe ser day, month o year");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        return response;
    }

    @GetMapping("/orders")
    public Map<String, Object> getOrdersStatistics(
            @RequestParam String period,
            @RequestParam int number) {

        String validatedPeriod = validate(period, number);
        List<Long> data;

        switch (validatedPeriod) {
            case "day": data = orderService.getDailyOrdersLastDays(number); break;
            case "month": data = orderService.getMonthlyOrdersLastMonths(number); break;
            case "year": data = orderService.getYearlyOrdersLastYears(number); break;
            default: throw new IllegalArgumentException("El periodo debe ser day, month o year");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        return response;
    }

    @GetMapping("/labels")
    public Map<String, Object> getLabelsStatistics(
            @RequestParam String period,
            @RequestParam int number) {

        String validatedPeriod = validate(period, number);
        List<String> data;

        switch (validatedPeriod) {
            case "day": data = orderService.getDailyLabelsLastDays(number); break;
            case "month": data = orderService.getMonthlyLabelsLastMonths(number); break;
            case "year": data = orderService.getYearlyLabelsLastYears(number); break;
            default: throw new IllegalArgumentException("El periodo debe ser day, month o year");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        return response;
    }

    @GetMapping("/categories")
    public Map<String, Object> getSalesByCategory() {
        Map<String, Object> response = new HashMap<>();
        response.put("data", orderService.getSalesByCategory());
        return response;
    }

    @GetMapping("/users/{userid}")
    public Map<String, Object> getMeanTicket(
            @PathVariable Long userid,
            @RequestParam String period,
            @RequestParam int number,
            HttpServletRequest request) {

        String validatedPeriod = validate(period, number);
        List<Double> data;

        User userSession = userService.findByEmail(request.getUserPrincipal().getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario autenticado no encontrado"));

        if (!request.isUserInRole("ADMIN") && !userSession.getId().equals(userid)) {
            throw new IllegalArgumentException("No tienes permiso para ver estas estadísticas");
        }

        if (!userService.findById(userid).isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        switch (validatedPeriod) {
            case "month":
                data = orderService.getMonthlyMeanTicketLastMonthsById(number, userid);
                break;
            case "year":
                data = orderService.getYearlyMeanTicketLastYearsById(number, userid);
                break;
            default:
                throw new IllegalArgumentException("El periodo debe ser month o year");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        return response;
    }

    private String validate(String period, int number) {
        if (number <= 0 || number > 100) {
            throw new IllegalArgumentException("El número de períodos debe ser mayor que cero");
        }
        if (period == null || (!period.equals("day") && !period.equals("month") && !period.equals("year"))) {
            throw new IllegalArgumentException("El periodo debe ser day, month o year");
        }
        return period;
    }
}
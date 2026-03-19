package es.dawgrupo2.zendashop.controller;

import java.util.List;

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
    public List<Double> getIncomeStatistics(
            @RequestParam String period,
            @RequestParam int number) {

        String validatedPeriod = validate(period, number);

        switch (validatedPeriod) {
            case "day" : return orderService.getDailyIncomeLastDays(number);
            case "month" : return orderService.getMonthlyIncomeLastMonths(number);
            case "year" : return orderService.getYearlyIncomeLastYears(number);
            default : throw new IllegalArgumentException("El periodo debe ser day, month o year");
        }
    }

    @GetMapping("/orders")
    public List<Long> getOrdersStatistics(
            @RequestParam String period,
            @RequestParam int number) {
        String validatedPeriod = validate(period, number);

        switch (validatedPeriod) {
            case "day" : return orderService.getDailyOrdersLastDays(number);
            case "month" : return orderService.getMonthlyOrdersLastMonths(number);
            case "year" : return orderService.getYearlyOrdersLastYears(number);
            default : throw new IllegalArgumentException("El periodo debe ser day, month o year");
        }
    }

    @GetMapping("/labels")
    public List<String> getLabelsStatistics(
            @RequestParam String period,
            @RequestParam int number) {
        String validatedPeriod = validate(period, number);


        switch (validatedPeriod) {
            case "day" : return orderService.getDailyLabelsLastDays(number);
            case "month" : return orderService.getMonthlyLabelsLastMonths(number);
            case "year" : return orderService.getYearlyLabelsLastYears(number);
            default : throw new IllegalArgumentException("El periodo debe ser day, month o year");
        }
    }

    @GetMapping("/users/{userid}")
    public List<Double> getMeanTicket(
            @PathVariable Long userid,
            @RequestParam String period,
            @RequestParam int number,
            HttpServletRequest request) {

        String validatedPeriod = validate(period, number);


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
                return orderService.getMonthlyMeanTicketLastMonthsById(number, userid);
            case "year":
                return orderService.getYearlyMeanTicketLastYearsById(number, userid);
            default:
                throw new IllegalArgumentException("El periodo debe ser month o year");
        }
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
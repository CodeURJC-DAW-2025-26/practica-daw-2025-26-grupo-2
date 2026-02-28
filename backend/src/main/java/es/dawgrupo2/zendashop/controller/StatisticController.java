package es.dawgrupo2.zendashop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


import es.dawgrupo2.zendashop.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.ObjectMapper;

@Controller
public class StatisticController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        if (request.getUserPrincipal() != null) {
            model.addAttribute("logged", true);
            model.addAttribute("username", request.getUserPrincipal().getName());
            model.addAttribute("admin", request.isUserInRole("ADMIN"));
        } else {
            model.addAttribute("logged", false);
        }
    }

    @GetMapping("/statistics")
    public String showStatistics(Model model) {
        
        // time
        LocalDate today = LocalDate.now();
        Locale spanishLocale = new Locale("es", "ES");

        LocalDate yesterday = today.minusDays(1);
        LocalDate lastMonth = today.minusMonths(1);
        LocalDate lastYear = today.minusYears(1);

        // range of a day
        LocalDateTime startOfDay = yesterday.atStartOfDay(); // 2024-03-20 00:00:00
        LocalDateTime endOfDay = yesterday.atTime(23, 59, 59); // 2024-03-20 23:59:59

        model.addAttribute("previousDay", yesterday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("previousMonth", lastMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", spanishLocale)));
        model.addAttribute("previousYear", lastYear.format(DateTimeFormatter.ofPattern("yyyy")));
        
        // Incomes
        Double dayIncome = orderService.sumIncomeBetween(startOfDay, endOfDay);
        model.addAttribute("previousDayIncome", dayIncome != null ? dayIncome : 0.0);

        Double monthIncome = orderService.sumIncomeByMonth(lastMonth.getMonthValue(), lastMonth.getYear());
        model.addAttribute("previousMonthIncome", monthIncome != null ? monthIncome : 0.0);

        Double yearIncome = orderService.sumIncomeByYear(lastYear.getYear());
        model.addAttribute("previousYearIncome", yearIncome != null ? yearIncome : 0.0);

        // Orders
        model.addAttribute("previousDayOrders", orderService.countOrdersBetween(startOfDay, endOfDay));
        model.addAttribute("previousMonthOrders", orderService.countByMonth(lastMonth.getMonthValue(), lastMonth.getYear()));

        model.addAttribute("dailyLabelsJson", toJson(orderService.getDailyLabelsLastDays(30)));
        model.addAttribute("dailyIncomeJson", toJson(orderService.getDailyIncomeLastDays(30)));
        model.addAttribute("dailyOrdersJson", toJson(orderService.getDailyOrdersLastDays(30)));

        model.addAttribute("monthlyLabelsJson", toJson(orderService.getMonthlyLabelsLastMonths(12)));
        model.addAttribute("monthlyIncomeJson", toJson(orderService.getMonthlyIncomeLastMonths(12)));
        model.addAttribute("monthlyOrdersJson", toJson(orderService.getMonthlyOrdersLastMonths(12)));

        model.addAttribute("yearlyLabelsJson", toJson(orderService.getYearlyLabelsLastYears(6)));
        model.addAttribute("yearlyIncomeJson", toJson(orderService.getYearlyIncomeLastYears(6)));
        model.addAttribute("yearlyOrdersJson", toJson(orderService.getYearlyOrdersLastYears(6)));

        return "statistics";
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "[]";
        }
    }
}
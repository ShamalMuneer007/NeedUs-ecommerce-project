package com.needus.ecommerce.controllers.admin;

import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user_order.OrderItem;
import com.needus.ecommerce.entity.user_order.UserOrder;
import com.needus.ecommerce.entity.user_order.enums.OrderStatus;
import com.needus.ecommerce.exceptions.TechnicalIssueException;
import com.needus.ecommerce.model.user_order.OrderItemDao;
import com.needus.ecommerce.model.user_order.OrderItemProjection;
import com.needus.ecommerce.service.product.ProductService;
import com.needus.ecommerce.service.user.OrderItemService;
import com.needus.ecommerce.service.user.UserInformationService;
import com.needus.ecommerce.service.user.UserLogService;
import com.needus.ecommerce.service.user.UserOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/dashboard")
@Slf4j
public class DashboardController {
    @Autowired
    UserOrderService orderService;

    @Autowired
    UserInformationService userService;
    @Autowired
    ProductService productService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    UserLogService userLogService;
    @GetMapping("/sales-report")
    public String dashboard(Model model,
                            HttpServletRequest request,
                            RedirectAttributes ra){
        Map<Products, Long> bestSellingProducts = orderItemService.findAllGroupByProduct().stream()
            .collect(Collectors.toMap(
                item -> productService.findProductById(item.getProductId()),
                OrderItemProjection::getQuantity,
                Long::sum));
        LinkedHashMap<Products, Long> sortedBestProducts = bestSellingProducts.entrySet().stream()
            .sorted(Map.Entry.<Products, Long>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new));
        List<UserOrder> orders = orderService.findAllOrders();
        Double totalRevenue = orderService.findAllDeliveredOrders().stream().mapToDouble(UserOrder::getTotalAmount).sum();
        model.addAttribute("orders",orders);
        model.addAttribute("totalRevenue",totalRevenue.intValue());
        model.addAttribute("noOfOrders",orderService.findAllOrders().size());
        model.addAttribute("noOfCustomers",userService.findAllUsers().size());
        model.addAttribute("bestProducts",sortedBestProducts);
        model.addAttribute("totalVisits",userLogService.totalVisitsCount());
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/dashboard";
    }
    @GetMapping("/sales-report/monthly")
    @ResponseBody
    public ResponseEntity<Map<String,List>> calculateMonthlyTotalAmount(){
        try {
            Map<Integer, Float> totalAmountOfEachMonth = new HashMap<>();
            Map<Integer, Long> totalQuantityOfEachMonth = new HashMap<>();
            for (int i = 1; i <= 12; i++) {
                totalAmountOfEachMonth.put(i, 0f);
                totalQuantityOfEachMonth.put(i, 0L);
            }

            orderService.findAllOrders().forEach(orders -> {
                if (orders.getOrderStatus().equals(OrderStatus.DELIVERED)) {
                    int month = orders.getOrderPlacedAt().getMonthValue();
                    float currentTotal = totalAmountOfEachMonth.get(month);
                    long currentQuantity = totalQuantityOfEachMonth.get(month);
                    totalAmountOfEachMonth.put(month, currentTotal + orders.getTotalAmount());
                    totalQuantityOfEachMonth.put(month, currentQuantity + orders.getOrderItems().stream()
                        .map(OrderItem::getQuantity).reduce(0, Integer::sum));
                }
            });
            ArrayList<Float> totalAmountOfMonth = new ArrayList<>();
            ArrayList<Long> totalQuantityOfMonth = new ArrayList<>();
            for (int month : totalAmountOfEachMonth.keySet()) {
                totalAmountOfMonth.add(totalAmountOfEachMonth.get(month));
                totalQuantityOfMonth.add(totalQuantityOfEachMonth.get(month));
            }
            Map<String, List> result = new HashMap<>();
            result.put("amount", totalAmountOfMonth);
            result.put("quantity", totalQuantityOfMonth);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("Something went wrong while calculating Monthly Total Amount");
            throw new TechnicalIssueException("Something went wrong while calculating Monthly Total Amount");
        }
    }
    @GetMapping("/sales-report/yearly")
    @ResponseBody
    public ResponseEntity<Map<String,List>> calculateYearlyTotalAmount(){
        try {
            Map<Integer, Float> totalAmountOfEachYear = new HashMap<>();
            Map<Integer, Long> totalQuantitySoldEachYear = new HashMap<>();
            for (int i = 2023; i <= 2033; i++) {
                totalAmountOfEachYear.put(i, 0f);
                totalQuantitySoldEachYear.put(i, 0L);
            }

            orderService.findAllOrders().forEach(orders -> {
                if (orders.getOrderStatus().equals(OrderStatus.DELIVERED)) {
                    int year = orders.getOrderPlacedAt().getYear();
                    float currentTotal = totalAmountOfEachYear.get(year);
                    long currentTotalQuantity = totalQuantitySoldEachYear.get(year);
                    totalAmountOfEachYear.put(year, currentTotal + orders.getTotalAmount());
                    totalQuantitySoldEachYear.put(year,
                        currentTotalQuantity + orders.getOrderItems()
                            .stream()
                            .map(OrderItem::getQuantity).reduce(0, Integer::sum));
                }
            });
            List<Integer> sortedYears = new ArrayList<>(totalAmountOfEachYear.keySet());
            sortedYears.sort(Comparator.naturalOrder());
            ArrayList<Float> totalAmountOfYear = new ArrayList<>();
            ArrayList<Long> totalQuantitySoldInTheYear = new ArrayList<>();
            for (int year : sortedYears) {
                totalAmountOfYear.add(totalAmountOfEachYear.get(year));
                totalQuantitySoldInTheYear.add(totalQuantitySoldEachYear.get(year));
            }
            Map<String, List> result = new HashMap<>();
            result.put("amount", totalAmountOfYear);
            result.put("quantity", totalQuantitySoldInTheYear);
            log.info(" " + totalAmountOfEachYear.keySet());
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("Something went wrong while calculating Yearly Total Amount");
            throw new TechnicalIssueException("Something went wrong while calculating Yearly Total Amount");
        }
    }
    @GetMapping("/sales-report/weekly")
    @ResponseBody
    public ResponseEntity<Map<String,List>> calculateWeeklyTotalAmount() {
        try {
            Map<Integer, Float> totalAmountOfEachWeek = new HashMap<>();
            Map<Integer, Long> totalQuantityOfEachWeek = new HashMap<>();
            // Get the current year and month
            YearMonth currentYearMonth = YearMonth.now();

            // Iterate over the days of the current month
            for (int day = 1; day <= currentYearMonth.lengthOfMonth(); day++) {
                LocalDate currentDate = currentYearMonth.atDay(day);
                log.info("" + currentDate);
                // Get the week number for the current date
                int weekNumberWithinYear = currentDate.get(WeekFields.ISO.weekOfWeekBasedYear());
                int weekNumberWithinMonth = weekNumberWithinYear - currentDate.withDayOfMonth(1).get(WeekFields.ISO.weekOfWeekBasedYear()) + 1;
                // Calculate total amount for the current week
                float currentTotalAmount = totalAmountOfEachWeek.getOrDefault(weekNumberWithinMonth, 0f);
                long currentTotalQuantity = totalQuantityOfEachWeek.getOrDefault(weekNumberWithinMonth, 0L);
                // Get orders placed on the current date
                List<UserOrder> ordersOnDate = orderService.findOrdersByDate(currentDate);
                // Calculate total amount for the current date and update the total for the week
                float totalAmountOnDate = ordersOnDate.stream()
                    .filter(order -> order.getOrderStatus().equals(OrderStatus.DELIVERED))
                    .map(UserOrder::getTotalAmount)
                    .reduce(0f, Float::sum);
                long totalQuantityOnDate = ordersOnDate.stream()
                    .filter(order -> order.getOrderStatus().equals(OrderStatus.DELIVERED))
                    .flatMapToInt(order -> order.getOrderItems().stream().mapToInt(OrderItem::getQuantity))
                    .sum();
                log.info("" + totalAmountOnDate);
                totalAmountOfEachWeek.put(weekNumberWithinMonth, currentTotalAmount + totalAmountOnDate);
                totalQuantityOfEachWeek.put(weekNumberWithinMonth, currentTotalQuantity + totalQuantityOnDate);
            }

            // Convert the map values to a list
            List<Float> totalAmountOfWeeks = new ArrayList<>(totalAmountOfEachWeek.values());
            List<Long> totalQuantityOfWeeks = new ArrayList<>(totalQuantityOfEachWeek.values());
            Map<String, List> result = new HashMap<>();
            result.put("quantity", totalQuantityOfWeeks);
            result.put("amount", totalAmountOfWeeks);
            log.info("" + totalAmountOfWeeks);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("Something went wrong while calculating Weekly Total Amount");
            throw new TechnicalIssueException("Something went wrong while calculating Weekly Total Amount");
        }
    }
}

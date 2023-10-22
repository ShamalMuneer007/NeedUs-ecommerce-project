package com.needus.ecommerce.controllers.admin;

import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user_order.OrderItem;
import com.needus.ecommerce.entity.user_order.UserOrder;
import com.needus.ecommerce.entity.user_order.enums.OrderStatus;
import com.needus.ecommerce.model.user_order.OrderItemDao;
import com.needus.ecommerce.model.user_order.OrderItemProjection;
import com.needus.ecommerce.service.product.ProductService;
import com.needus.ecommerce.service.user.OrderItemService;
import com.needus.ecommerce.service.user.UserInformationService;
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
        Double totalRevenue = orderService.findAllDeliveredOrders().stream().mapToDouble(UserOrder::getTotalAmount).sum();
        model.addAttribute("totalRevenue",totalRevenue.intValue());
        model.addAttribute("noOfOrders",orderService.findAllOrders().size());
        model.addAttribute("noOfCustomers",userService.findAllUsers().size());
        model.addAttribute("bestProducts",sortedBestProducts);
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/dashboard";
    }
    @GetMapping("/sales-report/monthly")
    @ResponseBody
    public ResponseEntity<List<Float>> calculateMonthlyTotalAmount(){
        Map<Integer, Float> totalAmountOfEachMonth = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            totalAmountOfEachMonth.put(i, 0f);
        }

        orderService.findAllOrders().forEach(orders -> {
            if(orders.getOrderStatus().equals(OrderStatus.DELIVERED)) {
                int month = orders.getOrderPlacedAt().getMonthValue();
                float currentTotal = totalAmountOfEachMonth.get(month);
                totalAmountOfEachMonth.put(month, currentTotal + orders.getTotalAmount());
            }
        });
        ArrayList<Float> totalAmountOfMonth = new ArrayList<>();
        for(int month : totalAmountOfEachMonth.keySet()){
            totalAmountOfMonth.add(totalAmountOfEachMonth.get(month));
        }
        return new ResponseEntity<>(totalAmountOfMonth, HttpStatus.OK);
    }
    @GetMapping("/sales-report/yearly")
    @ResponseBody
    public ResponseEntity< List<Float>> calculateYearlyTotalAmount(){
        Map<Integer, Float> totalAmountOfEachYear = new HashMap<>();
        for (int i = 2023; i <= 2033; i++) {
            totalAmountOfEachYear.put(i, 0f);
        }

        orderService.findAllOrders().forEach(orders -> {
            if(orders.getOrderStatus().equals(OrderStatus.DELIVERED)) {
                int year = orders.getOrderPlacedAt().getYear();
                float currentTotal = totalAmountOfEachYear.get(year);
                totalAmountOfEachYear.put(year, currentTotal + orders.getTotalAmount());
            }
        });
        List<Integer> sortedYears = new ArrayList<>(totalAmountOfEachYear.keySet());
        sortedYears.sort(Comparator.naturalOrder());
        ArrayList<Float> totalAmountOfYear = new ArrayList<>();
        for(int year : sortedYears){
            totalAmountOfYear.add(totalAmountOfEachYear.get(year));
        }
        log.info(" "+totalAmountOfEachYear.keySet());
        return new ResponseEntity<>(totalAmountOfYear, HttpStatus.OK);
    }
//    @GetMapping("/sales-report/weekly")
//    @ResponseBody
//    public ResponseEntity< List<Float>> calculateWeeklyTotalAmount(){
//        Map<Integer, Float> totalAmountOfEachYear = new HashMap<>();
//        for (int i = 2023; i <= 2033; i++) {
//            totalAmountOfEachYear.put(i, 0f);
//        }
//
//        orderService.findAllOrders().forEach(orders -> {
//            if(orders.getOrderStatus().equals(OrderStatus.DELIVERED)) {
//                int year = orders.getOrderPlacedAt().getYear();
//                float currentTotal = totalAmountOfEachYear.get(year);
//                totalAmountOfEachYear.put(year, currentTotal + orders.getTotalAmount());
//            }
//        });
//        List<Integer> sortedYears = new ArrayList<>(totalAmountOfEachYear.keySet());
//        sortedYears.sort(Comparator.naturalOrder());
//        ArrayList<Float> totalAmountOfYear = new ArrayList<>();
//        for(int year : sortedYears){
//            totalAmountOfYear.add(totalAmountOfEachYear.get(year));
//        }
//        log.info(" "+totalAmountOfEachYear.keySet());
//        return new ResponseEntity<>(totalAmountOfYear, HttpStatus.OK);
//    }
    @GetMapping("/sales-report/weekly")
    @ResponseBody
    public ResponseEntity<List<Float>> calculateWeeklyTotalAmount() {
        Map<Integer, Float> totalAmountOfEachWeek = new HashMap<>();

        // Get the current year and month
        YearMonth currentYearMonth = YearMonth.now();

        // Iterate over the days of the current month
        for (int day = 1; day <= currentYearMonth.lengthOfMonth(); day++) {
            LocalDate currentDate = currentYearMonth.atDay(day);

            // Get the week number for the current date
            int weekNumber = currentDate.get(WeekFields.ISO.weekOfWeekBasedYear());

            // Calculate total amount for the current week
            float currentTotal = totalAmountOfEachWeek.getOrDefault(weekNumber, 0f);

            // Get orders placed on the current date
            List<UserOrder> ordersOnDate = orderService.findOrdersByDate(currentDate);

            // Calculate total amount for the current date and update the total for the week
            float totalAmountOnDate = ordersOnDate.stream()
                .filter(order -> order.getOrderStatus().equals(OrderStatus.DELIVERED))
                .map(UserOrder::getTotalAmount)
                .reduce(0f, Float::sum);

            totalAmountOfEachWeek.put(weekNumber, currentTotal + totalAmountOnDate);
        }

        // Convert the map values to a list
        List<Float> totalAmountOfWeeks = new ArrayList<>(totalAmountOfEachWeek.values());
        log.info(""+totalAmountOfWeeks);
        return new ResponseEntity<>(totalAmountOfWeeks, HttpStatus.OK);
    }
}

package com.needus.ecommerce.controllers;

import com.needus.ecommerce.entity.user.order.OrderItem;
import com.needus.ecommerce.entity.user.order.UserOrder;
import com.needus.ecommerce.service.user.UserOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@Slf4j
@RequestMapping("/admin/order")
public class AdminOrderController {
    @Autowired
    UserOrderService orderService;
    @GetMapping("/list")
    public String orderList(HttpServletRequest request,
                            Model model){
        List<UserOrder> orders = orderService.findAllOrders();
        model.addAttribute("orders",orders);
        model.addAttribute("requestURI", request.getRequestURI());
        return "admin/orders";
    }
    @GetMapping("/order-details/{orderId}")
    public String orderDetails(@PathVariable(name="orderId") long orderId,
                               Model model,
                               HttpServletRequest request){
        UserOrder orderDetails = orderService.findOrderDetailsById(orderId);
        List<OrderItem> orderItems = orderDetails.getOrderItems();
        model.addAttribute("orderDetails",orderDetails);
        model.addAttribute("orderItems",orderItems);
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/orderDetails";
    }
    @PostMapping("/order-details/change-orderStatus/{orderId}")
    public String changeOrderStatus(@RequestParam(name="status",required = false) String value,
                                    @PathVariable(name="orderId") Long orderId,
                                    RedirectAttributes ra){
        if(Objects.isNull(value)){
            return "redirect:/admin/order/order-details/"+orderId;
        }
        orderService.changeOrderStatus(value,orderId);
        if(value.equalsIgnoreCase("1")){
            ra.addFlashAttribute("message","The order status has been changed to processing");
            return "redirect:/admin/order/order-details/"+orderId;
        }
        else if(value.equalsIgnoreCase("2")){
            ra.addFlashAttribute("message","The order status has been changed to shipped");
            return "redirect:/admin/order/order-details/"+orderId;
        }
        else if(value.equalsIgnoreCase("3")){
            ra.addFlashAttribute("message","The order status has been changed to delivered");
            return "redirect:/admin/order/order-details/"+orderId;
        }
        else if(value.equalsIgnoreCase("4")){
            ra.addFlashAttribute("message","The order status has been changed to cancelled");
            return "redirect:/admin/order/order-details/"+orderId;
        }
        return "redirect:/admin/order/order-details/"+orderId;
    }
}

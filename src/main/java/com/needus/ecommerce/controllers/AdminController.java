package com.needus.ecommerce.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/productlist")
    public String products(Model model, HttpServletRequest request){
        model.addAttribute("requestURI", request.getRequestURI());
        return "admin/products";
    }
    @GetMapping("/productlist/addproduct")
    public String addproducts(Model model, HttpServletRequest request){
        model.addAttribute("requestURI", request.getRequestURI());
        return "admin/add_product";
    }
}

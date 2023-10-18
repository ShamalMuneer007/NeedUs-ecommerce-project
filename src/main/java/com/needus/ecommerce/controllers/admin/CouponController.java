package com.needus.ecommerce.controllers.admin;

import com.needus.ecommerce.entity.product.Coupon;
import com.needus.ecommerce.service.product.CouponService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/admin/coupons")
public class CouponController {
    @Autowired
    CouponService couponService;
    @GetMapping("/list")
    public String couponList(Model model, HttpServletRequest request){
        List<Coupon> coupons = couponService.findAllNonDeletedCoupons();
        model.addAttribute("coupons",coupons);
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/couponList";
    }
    @GetMapping("/addCoupon")
    public String addCoupon(Model model,HttpServletRequest request){
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/addCoupon";
    }
    @PostMapping("/saveCoupon")
    public String saveCoupon(@ModelAttribute Coupon coupon,
                             @RequestParam(name="date") String expirationDate,
                             RedirectAttributes ra){
        log.info("coupon Code : "+coupon.getCouponCode());
        if(couponService.couponNameExists(coupon.getCouponName())){
            ra.addFlashAttribute("message","Coupon name already Exists");
            return "redirect:/admin/coupons/addCoupon";
        }
        if(couponService.couponCodeExists(coupon.getCouponCode())){
            ra.addFlashAttribute("message","Coupon code already Exists");
            return "redirect:/admin/coupons/addCoupon";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        coupon.setExpirationDate(LocalDate.parse(expirationDate, formatter));
        couponService.addCoupon(coupon);
        ra.addFlashAttribute("successMsg","Coupon is Successfully added");
        return "redirect:/admin/coupons/list";
    }
    @PostMapping("/delete/{couponId}")
    public String deleteCoupon(@PathVariable(name = "couponId") Long couponId,
                               RedirectAttributes ra){
        couponService.deleteCoupon(couponId);
        ra.addFlashAttribute("successMsg","Coupon is Successfully deleted");
        return "redirect:/admin/coupons/list";
    }
}

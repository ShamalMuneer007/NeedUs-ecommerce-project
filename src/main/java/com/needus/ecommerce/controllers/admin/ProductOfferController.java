package com.needus.ecommerce.controllers.admin;

import com.needus.ecommerce.entity.product.Categories;
import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.exceptions.ResourceNotFoundException;
import com.needus.ecommerce.model.product.ProductInfoDto;
import com.needus.ecommerce.service.product.BrandService;
import com.needus.ecommerce.service.product.CategoryService;
import com.needus.ecommerce.service.product.ProductService;
import com.needus.ecommerce.service.user.ReferralOfferService;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/admin/products/offers")
public class ProductOfferController {
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    BrandService brandService;

    @Autowired
    ReferralOfferService referralOfferService;
    @GetMapping("/offer-options")
    public String offerOptions(HttpServletRequest request, Model model){
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/offerOptions";
    }
    @GetMapping("/category-offers")
    public String categoryOffers(HttpServletRequest request,
                                 Model model){
        List<Categories> categories = categoryService.findAllNonDeletedCategories();
        model.addAttribute("requestURI",request.getRequestURI());
        model.addAttribute("categories",categories);
        return "admin/categoryOffers";
    }
    @PostMapping("/apply-category-offer")
    public String applyCategoryOffer(@RequestParam(name = "category") Long categoryId,
                                     @RequestParam(name = "discount") Float discountPercentage,
                                     @RequestParam(name = "expirationDate") String expiryDate,
                                     RedirectAttributes ra){
        categoryService.applyOffer(expiryDate,discountPercentage,categoryId);
        productService.applyOfferForCategory(categoryId,discountPercentage);
        ra.addFlashAttribute("message","Category Offer Applied Successfully");
        return "redirect:/admin/products/offers/offer-options";
    }
    @GetMapping("/product-offers")
    public String productOffers(HttpServletRequest request,
                                 Model model){
        List<Products> products = productService.findAllNonBlockedProducts();
        model.addAttribute("requestURI",request.getRequestURI());
        model.addAttribute("products",products);
        return "admin/productOffers";
    }

    @PostMapping("/apply-product-offer")
    public String applyProductOffer(@RequestParam(name = "product") Long productId,
                                     @RequestParam(name = "discount") Float discountPercentage,
                                     @RequestParam(name = "expirationDate") String expiryDate,
                                     RedirectAttributes ra){
        if(!productService.existsById(productId)){
            log.error("No Such Product");
            throw new ResourceNotFoundException("No such product");
        }
        productService.applyOfferForProduct(productId,discountPercentage,expiryDate);
        ra.addFlashAttribute("message","Product Offer Applied Successfully");
        return "redirect:/admin/products/offers/offer-options";
    }
    @GetMapping("/referral-offers")
    public String referralOffers(RedirectAttributes ra,HttpServletRequest request,Model model){
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/referralOffers";
    }
    @PostMapping("/apply-referral-offer")
    public String applyReferralOffers(@RequestParam(name = "referrer") Float referrerAmount,
                                      @RequestParam(name = "referee") Float refereeAmount,
                                      RedirectAttributes ra){
        referralOfferService.updateOffer(referrerAmount,refereeAmount);
        ra.addFlashAttribute("message","Referral Offer Applied Successfully");
        return "redirect:/admin/products/offers/offer-options";
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<ProductInfoDto>> searchProducts(@RequestParam String keyword) {
        List<Products> searchedProducts = productService.searchProducts(keyword);
        List<ProductInfoDto> productInfoDtos = new ArrayList<>();
        searchedProducts.forEach(product -> {
            ProductInfoDto productInfoDto = new ProductInfoDto(product.getProductName(),product.getProductId());
            productInfoDtos.add(productInfoDto);
        });
        return new ResponseEntity<>(productInfoDtos, HttpStatus.OK);
    }
}

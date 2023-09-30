package com.needus.ecommerce.controllers;

import com.needus.ecommerce.entity.product.Categories;
import com.needus.ecommerce.entity.product.ProductImages;
import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.exceptions.ResourceNotFoundException;
import com.needus.ecommerce.exceptions.TechnicalIssueException;
import com.needus.ecommerce.model.ProductDto;
import com.needus.ecommerce.service.product.CategoryService;
import com.needus.ecommerce.service.product.ProductImageService;
import com.needus.ecommerce.service.product.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;
    @GetMapping("/home")
    public String landingPage(Model model){
        log.info("Inside Landing Page");
        List<ProductDto> productsDto = new ArrayList<>();
        List<Categories> categories = categoryService.findAllCategories();
        log.info("fetched categories");
        List<Products> products;
        try {
            products = productService.findAllNonBlockedProducts();
        } catch (Exception e) {
            log.error("An error occurred while fetching the products",e);
            throw new TechnicalIssueException("An error occurred while fetching products", e);
        }
        for(Products product : products){
            productsDto.add(new ProductDto(product));
        }
        log.info("fetched products");
        model.addAttribute("categories",categories);
        model.addAttribute("products",productsDto);
        return "shop/home";
    }
    @GetMapping("/home/product-details/{id}")
    public String productDetails(
        @PathVariable(name = "id") Long productId ,
        Model model){
        log.info("Inside product details method");
        if(!productService.existsById(productId)){
            log.error("Product of the given path variable does not exists");
            throw new ResourceNotFoundException("Product not found");
        }
        Products product = productService.findProductById(productId);
        List<ProductImages> images = product.getImages();
        log.info("fetched images");
        model.addAttribute("image",images.get(0));
        model.addAttribute("images",images);
        model.addAttribute("product",product);

        return "shop/productDetails";
    }
    @GetMapping("/categories/{id}")
    public String categories(@PathVariable(name="id") Long categoryId, Model model){
        Categories category = categoryService.findCatgeoryById(categoryId);
        List<Categories> categories = categoryService.findAllCategories();
        List<Products> products = productService.findProductsOfCategory(categoryId);
        List<ProductDto> productDto = new ArrayList<>();
        for(Products product : products){
            productDto.add(new ProductDto(product));
        }
        model.addAttribute("empty",products.isEmpty());
        model.addAttribute("category",category);
        model.addAttribute("categories",categories);
        model.addAttribute("products",productDto);
        return "shop/categoryShopping";
    }

}
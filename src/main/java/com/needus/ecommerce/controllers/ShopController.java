package com.needus.ecommerce.controllers;

import com.needus.ecommerce.entity.product.Categories;
import com.needus.ecommerce.entity.product.ProductImages;
import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.exceptions.ResourceNotFoundException;
import com.needus.ecommerce.model.ProductDto;
import com.needus.ecommerce.service.product.CategoryService;
import com.needus.ecommerce.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @GetMapping("/home")
    public String landingPage(Model model){
        List<ProductDto> productsDto = new ArrayList<>();
        List<Categories> categories = categoryService.findAllCategories();
        List<Products> products = productService.findAllNonBlockedProducts();
        for(Products product : products){
            productsDto.add(new ProductDto(product));
        }
        model.addAttribute("categories",categories);
        model.addAttribute("products",productsDto);
        return "shop/home";
    }
    @GetMapping("/home/product-details/{id}")
    public String productDetails(@PathVariable(name = "id") Long productId , Model model){
        if(!productService.existsById(productId)){
            throw new ResourceNotFoundException("Product not found");
        }
        Products product = productService.findProductById(productId);
        List<ProductImages> images = product.getImages();
        model.addAttribute("image",images.get(0));
        model.addAttribute("images",images);
        model.addAttribute("product",product);
        return "shop/productDetails";
    }
}
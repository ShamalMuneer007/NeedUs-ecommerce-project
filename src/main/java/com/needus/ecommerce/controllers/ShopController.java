package com.needus.ecommerce.controllers;

import com.needus.ecommerce.entity.product.*;
import com.needus.ecommerce.exceptions.ResourceNotFoundException;
import com.needus.ecommerce.exceptions.TechnicalIssueException;
import com.needus.ecommerce.model.ProductDto;
import com.needus.ecommerce.service.product.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@Slf4j
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductFilterService filterService;
    @Autowired
    BrandService brandService;
    @GetMapping("/home")
    public String landingPage(Model model,
                              @RequestParam(name="search",required = false) String searchKey,
                              @RequestParam(defaultValue = "1") int pageNo,
                              @RequestParam(defaultValue = "10") int pageSize,
                              RedirectAttributes ra){
//        log.info("Authenticated user : "+ SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        log.info("Inside Landing Page : ");
        log.info("fetched categories");
        Page<Products> products;
        try {
            if(Objects.isNull(searchKey)) {
                products = productService.findAllNonBlockedProducts(pageNo, pageSize);
            }
            else{
                products = productService.searchProducts(pageNo,pageSize,searchKey);
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching the products",e);
            throw new TechnicalIssueException("An error occurred while fetching products", e);
        }
        if(products.isEmpty()){
            ra.addAttribute("searchKey",searchKey);
            return "redirect:/shop/home?noSuchProducts";
        }
        Page<ProductDto> productsDto = products.map(ProductDto::new);
        log.info("fetched products");
        model.addAttribute("filters",filterService.findAllFilters());
        model.addAttribute("brands",brandService.findAllNonDeletedBrands());
        model.addAttribute("categories", categoryService.findAllNonDeletedCategories());
        model.addAttribute("products",productsDto);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
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
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("image",images.get(0));
        model.addAttribute("images",images);
        model.addAttribute("product",product);

        return "shop/productDetails";
    }
    @GetMapping("/categories/{id}")
    public String categories(@PathVariable(name="id") Long categoryId,
                             @RequestParam(defaultValue = "1") int pageNo,
                             @RequestParam(defaultValue = "10") int pageSize,
                             @RequestParam(name = "search",required = false) String searchKey,
                             Model model,RedirectAttributes ra){
        Categories category = categoryService.findCatgeoryById(categoryId);
        Page<Products> products;
        List<ProductFilters> filters = filterService.findAllFiltersForCategory(categoryId);
        products = productService.findProductsOfCategory(categoryId,pageNo,pageSize);
        if(Objects.nonNull(searchKey)){
            products = productService.findProductBySearchKey(pageNo,pageSize,categoryId,searchKey);
            log.info("products exists : "+products.isEmpty());
            products.forEach(products1 -> log.info("products : "+products1.getProductName()));
            if(products.isEmpty()){
                ra.addAttribute("searchKey",searchKey);
                return "redirect:/shop/categories/"+categoryId+"?noSuchProductsInCategory&category="+ category.getCategoryName();
            }
        }
        Page<ProductDto> productDto = products.map(ProductDto::new);
        model.addAttribute("filters",filters);
        model.addAttribute("empty",products.isEmpty());
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("category",category);
        model.addAttribute("categories",categoryService.findAllNonDeletedCategories());
        model.addAttribute("brands",brandService.findAllNonDeletedBrands());
        model.addAttribute("products",productDto);
        return "shop/categoryShopping";
    }
    @GetMapping("/filterProducts")
    public String filterProduct(@RequestParam(name="max-price" , required = false) Long maxPrice,
                                @RequestParam(name="min-price", required = false) Long minPrice,
                                @RequestParam(name="brand",required = false) List<Brands> brands,
                                @RequestParam(name="filter",required = false) List<ProductFilters> productFilters,
                                @RequestParam(defaultValue = "1") int pageNo,
                                @RequestParam(defaultValue = "10") int pageSize,
                                Model model){
        if(Objects.isNull(minPrice)){
            minPrice = 0L;
        }
        if(Objects.isNull(maxPrice)){
            maxPrice = (long) Integer.MAX_VALUE;
        }
        log.info("Inside Landing Page : ");
        log.info("fetched categories");
        Page<Products> products;
        try {
            products = productService.findAllProductsWithtinParams(maxPrice,minPrice,productFilters,brands,pageNo,pageSize);
        } catch (Exception e) {
            log.error("An error occurred while fetching the products",e);
            throw new TechnicalIssueException("An error occurred while fetching products", e);
        }
        Page<ProductDto> productsDto = products.map(ProductDto::new);
        log.info("fetched products");
        model.addAttribute("filters",filterService.findAllFilters());
        model.addAttribute("categories",categoryService.findAllNonDeletedCategories());
        model.addAttribute("brands",brandService.findAllNonDeletedBrands());
        model.addAttribute("products",productsDto);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return "shop/filteredShopping";
    }

}
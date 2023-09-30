package com.needus.ecommerce.controllers;

import com.needus.ecommerce.entity.product.Brands;
import com.needus.ecommerce.entity.product.Categories;
import com.needus.ecommerce.service.product.BrandService;
import com.needus.ecommerce.service.product.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/admin/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @GetMapping("/list")
    public String listCategories(Model model,HttpServletRequest request){
        model.addAttribute("requestURI",request.getRequestURI());
        model.addAttribute("categories",categoryService.findAllCategories());
        model.addAttribute("brands",brandService.findAllBrands());
        return "admin/categoryList";
    }
    @GetMapping("/addCategory")
    public String addVariants(Model model, HttpServletRequest request){
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/addCategory";
    }
    @PostMapping("/saveCategory")
    public String saveVariants(
        @RequestParam(name = "categoryName") String categoryName,
        @RequestParam(name = "brandName") String brandName,
        RedirectAttributes ra
    ) {
        if(!categoryName.isEmpty()){
            if(categoryService.categoryExists(categoryName)){
                ra.addFlashAttribute("message","Category already exists");
                return "redirect:/admin/categories/list";
            }
            Categories categories = new Categories();
            categories.setCategoryName(categoryName);
            categoryService.saveCategory(categories);
        }

        if(!brandName.isEmpty()){
            if(brandService.brandExists(brandName)){
                ra.addFlashAttribute("message","Brand already exists");
                return "redirect:/admin/categories/list";
            }
            Brands brands = new Brands();
            brands.setBrandName(brandName);
            brandService.saveBrand(brands);
        }

        ra.addFlashAttribute("message","Category Added to the list successfully");
        return "redirect:/admin/categories/list";
    }
    @PostMapping("/deleteBrand/{id}")
    public String deleteBrand(@PathVariable(name="id") Long brandId,RedirectAttributes ra){
        brandService.deleteBrandById(brandId);
        ra.addFlashAttribute("message","Brand removed from the list successfully");
        return "redirect:/admin/categories/list";
    }
    @PostMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable(name="id") Long categoryId,RedirectAttributes ra){
        categoryService.deleteCategoryById(categoryId);
        ra.addFlashAttribute("message","Category removed from the list successfully");
        return "redirect:/admin/categories/list";
    }
}

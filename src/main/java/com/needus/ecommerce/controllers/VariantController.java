package com.needus.ecommerce.controllers;

import com.needus.ecommerce.entity.product.Categories;
import com.needus.ecommerce.entity.product.ProductFilters;
import com.needus.ecommerce.service.product.CategoryService;
import com.needus.ecommerce.service.product.ProductFilterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/variants")
public class VariantController {
    @Autowired
    private ProductFilterService filterService;
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/list")
    public String variants(Model model, HttpServletRequest request){
        model.addAttribute("requestURI",request.getRequestURI());
        List<Categories> categories = categoryService.findAllCategories();
        model.addAttribute("category",categories);
        return "admin/addVariants";
    }
    @PostMapping("/saveVariant")
    public String addVariants(
        @RequestParam(name="categoryId") Long categoryId,
        @RequestParam(name="filterName") String filterName,
        RedirectAttributes ra
    ){
        Categories category = categoryService.findCatgeoryById(categoryId);
        if(filterService.variantExistInCategory(categoryId,filterName)){
            ra.addFlashAttribute("message","Variant in that category is already present");
            return "redirect:/admin/variants";
        }
        ProductFilters productFilters =  new ProductFilters();
        productFilters.setCategory(category);
        productFilters.setFilterName(filterName);
        filterService.saveFilter(productFilters);
        ra.addFlashAttribute("message","Your Variant is successfully added to the list");
        return "redirect:/admin/variants/list";
    }
}

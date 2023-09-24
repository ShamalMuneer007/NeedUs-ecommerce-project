package com.needus.ecommerce.controllers;

import com.needus.ecommerce.entity.product.*;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.model.ProductDto;
import com.needus.ecommerce.repository.product.ProductsRepository;
import com.needus.ecommerce.repository.user.UserInformationRepository;
import com.needus.ecommerce.service.product.*;
import com.needus.ecommerce.service.user.UserInformationService;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    //Field Injections
    @Autowired
    private UserInformationService userInformationService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductFilterService filterService;
    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ProductsRepository productsRepository;

//    @Autowired
//    private ProductFilterService filterService;

    //User Controller Methods
    @GetMapping("/users")
    public String users(Model model, HttpServletRequest request){
        model.addAttribute("requestURI", request.getRequestURI());
        List<UserInformation> user = userInformationService.findAllUsers();
        model.addAttribute("user",user);
        return "admin/customerList";
    }
    @PostMapping("/users/block/{id}")
    public String userBlock(@PathVariable(name = "id")UUID id, RedirectAttributes redirectAttributes){
        userInformationService.blockUser(id);
        if(userInformationService.findUserById(id).isEnabled()) {
            redirectAttributes.addFlashAttribute("successMsg", "User is enabled");
        }
        else{
            redirectAttributes.addFlashAttribute("successMsg", "User is disabled");

        }
        return "redirect:/admin/users";
    }
    @PostMapping("/users/delete/{id}")
    public String userDelete(@PathVariable(name = "id")UUID id, RedirectAttributes redirectAttributes){
        userInformationService.deleteUserById(id);
        redirectAttributes.addFlashAttribute("successMsg","User is deleted Successfully");
        return "redirect:/admin/users";
    }
    @GetMapping("/users/edit/{id}")
    public String userEdit(@PathVariable(name = "id") UUID id ,Model model, HttpServletRequest request){
        UserInformation user = userInformationService.findUserById(id);
        model.addAttribute("user",user);
        model.addAttribute("requestURI", request.getRequestURI());
        return "admin/editCustomer";
    }
    @PostMapping("/users/edit/editConfirmed/{id}")
    public String updateUser(@ModelAttribute UserInformation user, @PathVariable(name="id") UUID id , RedirectAttributes redirectAttributes){
        UserInformation userDetails = userInformationService.findUserById(id);
        if
        (userInformationRepository.existsByUsername(user.getUsername())&&
        !user.getUsername().equals(userDetails.getUsername())){
            redirectAttributes.addFlashAttribute("errorMessage","Username exists already");
            return "redirect:/admin/users/edit/{id}";
        }
        if(userInformationRepository.existsByEmail(user.getEmail())&&
            !user.getEmail().equals(userDetails.getEmail())){
            redirectAttributes.addFlashAttribute("errorMessage","email exists already");
            return "redirect:/admin/users/edit/{id}";
        }
        userInformationService.updateUser(id,user);
        redirectAttributes.addFlashAttribute("successMsg","User is deleted Successfully");
        return "redirect:/admin/users";
    }


    //Product Controller Methods

    @GetMapping("/products")
    public String products(Model model, HttpServletRequest request){
        List<ProductDto> productDto =  new ArrayList<>();
        List<Products> products = productService.findAllProducts();
        for(Products product : products){
            productDto.add(new ProductDto(product));
            System.out.println(product.getProductName());
        }
        model.addAttribute("requestURI", request.getRequestURI());
        model.addAttribute("products",productDto);
        return "admin/products";
    }
    @GetMapping("/products/addProduct")
    public String addProducts(Model model, HttpServletRequest request){
        List<Categories> categories = categoryService.findAllCategories();
        List<Brands> brands = brandService.findAllBrands();
        List<ProductFilters> filters = filterService.findAllFilters();
        model.addAttribute("requestURI", request.getRequestURI());
        model.addAttribute("category",categories);
        model.addAttribute("brand",brands);
        model.addAttribute("filter",filters);
        return "admin/add_product";
    }
    @PostMapping("/products/addProduct/save")
    public String saveProduct(
        @RequestParam(name="brandId") Long brandId,
        @RequestParam(name="categoryId") Long categoryId,
        @RequestParam(name="productName") String productName,
        @RequestParam(name="description") String description,
        @RequestParam(name="productImages") List<MultipartFile> imageFiles,
        @RequestParam(name="productPrice") Float price,
        @RequestParam(name="productStock") Integer stock,
        @RequestParam(name="productFilters") List<ProductFilters> filters,
        Model model,RedirectAttributes ra
    ) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        UserInformation currentUserInfo = userInformationService.findUserByName(currentUser);
        System.out.println(currentUserInfo.getUserId());
        Brands brand = brandService.findBrandById(brandId);
        Categories category = categoryService.findCatgeoryById(categoryId);
        Products product = new Products();
        product.setProductName(productName);
        Products tempProduct = productService.save(product);
        List<ProductImages> images = new ArrayList<>();
        for(MultipartFile imageFile : imageFiles){
            String fileName = fileUploadDir(imageFile);
            ProductImages imageObj =  new ProductImages(fileName,tempProduct);
            imageObj = productImageService.save(imageObj);
            images.add(imageObj);
        }

        product.setImages(images);
        product.setDescription(description);
        product.setBrands(brand);
        product.setCategories(category);
        product.setUserInformation(currentUserInfo);
        product.setStock(stock);
        product.setProductFilters(filters);

        productService.save(product);
        ra.addFlashAttribute("message","Product added successfully");
        return "redirect:/admin/products/addProduct";
    }

    //Category Controller Methods

    @GetMapping("/categories")
    public String addVariants(Model model,HttpServletRequest request){
        model.addAttribute("requestURI",request.getRequestURI());
        return "admin/addCategory";
    }
    @PostMapping("/categories/saveCategory")
    public String saveVariants(
        @RequestParam(name = "categoryName") String categoryName,
        @RequestParam(name = "brandName") String brandName,
        RedirectAttributes ra
    ) {
        if(!categoryName.isEmpty()){
            Categories categories = new Categories();
            categories.setCategoryName(categoryName);
            categoryService.saveCategory(categories);
        }

        if(!brandName.isEmpty()){
            Brands brands = new Brands();
            brands.setBrandName(brandName);
            brandService.saveBrand(brands);
        }

        ra.addFlashAttribute("message","Category Added to the list successfully");


        return "redirect:/admin/categories";
    }

    //Variant Controller Methods

    @GetMapping("/variants")
    public String variants(Model model, HttpServletRequest request){
        model.addAttribute("requestURI",request.getRequestURI());
        List<Categories> categories = categoryService.findAllCategories();
        model.addAttribute("category",categories);
        return "admin/addVariants";
    }
    @PostMapping("/variants/saveVariant")
    public String addVariants(
        @RequestParam(name="categoryId") Long categoryId,
        @RequestParam(name="filterName") String filterName,
        RedirectAttributes ra
    ){
        Categories category = categoryService.findCatgeoryById(categoryId);
        ProductFilters productFilters =  new ProductFilters();
        productFilters.setCategory(category);
        productFilters.setFilterName(filterName);
        filterService.saveFilter(productFilters);
        ra.addFlashAttribute("message","Your Variant is successfully added to the list");
        return "redirect:/admin/variants";
    }

    // Image upload directory method
    private String fileUploadDir(MultipartFile file) throws IOException {
        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/img/uploads";

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // Save the file to the upload directory
        String filePath = uploadDir + "/" + fileName;
        Path path = Paths.get(filePath);
        System.out.println(path);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // Return the file path
        return fileName;
    }
}


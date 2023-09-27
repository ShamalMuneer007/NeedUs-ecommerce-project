package com.needus.ecommerce.controllers;

import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.repository.product.ProductsRepository;
import com.needus.ecommerce.service.product.*;
import com.needus.ecommerce.service.user.UserInformationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    //Field Injections
    @Autowired
    private UserInformationService userInformationService;

    @GetMapping("/users/list")
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
        return "redirect:/admin/users/list";
    }
    @PostMapping("/users/delete/{id}")
    public String userDelete(@PathVariable(name = "id")UUID id, RedirectAttributes redirectAttributes){
        userInformationService.deleteUserById(id);
        redirectAttributes.addFlashAttribute("successMsg","User is deleted Successfully");
        return "redirect:/admin/users/list";
    }
//    @GetMapping("/users/edit/{id}")
//    public String userEdit(@PathVariable(name = "id") UUID id ,Model model, HttpServletRequest request){
//        UserInformation user = userInformationService.findUserById(id);
//        model.addAttribute("user",user);
//        model.addAttribute("requestURI", request.getRequestURI());
//        return "admin/editCustomer";
//    }
//    @PostMapping("/users/edit/editConfirmed/{id}")
//    public String updateUser(@ModelAttribute UserInformation user, @PathVariable(name="id") UUID id , RedirectAttributes redirectAttributes){
//        UserInformation userDetails = userInformationService.findUserById(id);
//        if
//        (userInformationRepository.existsByUsername(user.getUsername())&&
//        !user.getUsername().equals(userDetails.getUsername())){
//            redirectAttributes.addFlashAttribute("errorMessage","Username exists already");
//            return "redirect:/admin/users/edit/{id}";
//        }
//        if(userInformationRepository.existsByEmail(user.getEmail())&&
//            !user.getEmail().equals(userDetails.getEmail())){
//            redirectAttributes.addFlashAttribute("errorMessage","email exists already");
//            return "redirect:/admin/users/edit/{id}";
//        }
//        userInformationService.updateUser(id,user);
//        redirectAttributes.addFlashAttribute("successMsg","User is deleted Successfully");
//        return "redirect:/admin/users";
//    }


    //Product Controller Methods

//    @GetMapping("/products")
//    public String products(Model model, HttpServletRequest request){
//        List<ProductDto> productDto =  new ArrayList<>();
//        List<Products> products = productService.findAllProducts();
//        for(Products product : products){
//            productDto.add(new ProductDto(product));
//        }
//        model.addAttribute("requestURI", request.getRequestURI());
//        model.addAttribute("products",productDto);
//        return "admin/products";
//    }
//    @GetMapping("/products/addProduct")
//    public String addProducts(Model model, HttpServletRequest request){
//        List<Categories> categories = categoryService.findAllCategories();
//        List<Brands> brands = brandService.findAllBrands();
//        List<ProductFilters> filters = filterService.findAllFilters();
//        model.addAttribute("requestURI", request.getRequestURI());
//        model.addAttribute("category",categories);
//        model.addAttribute("brand",brands);
//        model.addAttribute("filter",filters);
//        return "admin/add_product";
//    }
//    @PostMapping("/products/addProduct/save")
//    public String saveProduct(
//        @RequestParam(name="brandId") Long brandId,
//        @RequestParam(name="categoryId") Long categoryId,
//        @RequestParam(name="productName") String productName,
//        @RequestParam(name="description") String description,
//        @RequestParam(name="productImages") List<MultipartFile> imageFiles,
//        @RequestParam(name="productPrice") Float price,
//        @RequestParam(name="productStock") Integer stock,
//        @RequestParam(name="productFilters") List<ProductFilters> filters,
//        Model model,RedirectAttributes ra
//    ) throws IOException {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String currentUser = auth.getName();
//        UserInformation currentUserInfo = userInformationService.findUserByName(currentUser);
//        Brands brand = brandService.findBrandById(brandId);
//        Categories category = categoryService.findCatgeoryById(categoryId);
//        Products product = new Products();
//        product.setProductName(productName);
//        Products tempProduct = productService.save(product);
//        List<ProductImages> images = new ArrayList<>();
//        for(MultipartFile imageFile : imageFiles){
//            String fileName = fileUploadDir(imageFile);
//            ProductImages imageObj =  new ProductImages(fileName,tempProduct);
//            imageObj = productImageService.save(imageObj);
//            images.add(imageObj);
//        }
//
//        product.setImages(images);
//        product.setDescription(description);
//        product.setBrands(brand);
//        product.setCategories(category);
//        product.setProductPrice(price);
//        product.setUserInformation(currentUserInfo);
//        product.setStock(stock);
//        product.setProductFilters(filters);
//
//        productService.save(product);
//        ra.addFlashAttribute("message","Product added successfully");
//        return "redirect:/admin/products/addProduct";
//    }
//
//    @PostMapping("/products/block/{id}")
//    public String productBlock(RedirectAttributes redirectAttributes,
//                               @PathVariable(name="id") Long productId){
//        productService.blockProduct(productId);
//        Products products = productService.findProductById(productId);
//        if(!products.isProductStatus()) {
//            redirectAttributes.addFlashAttribute("message", "Product is Blocked");
//        }
//        else {
//            redirectAttributes.addFlashAttribute("message","Product is Active");
//        }
//        return "redirect:/admin/products";
//    }
//    @GetMapping("/products/editProduct/{id}")
//    public String productEdit(@PathVariable(name = "id") Long id,Model model){
//        if(!productService.existsById(id)){
//            throw new ResourceNotFoundException("Product not found");
//        }
//        Products product = productService.findProductById(id);
//        Brands productBrand = product.getBrands();
//        Categories productCategories = product.getCategories();
////        List<ProductFilters> productFilters = product.getProductFilters();
//        List<Brands> brands = brandService.findAllBrands();
//        List<Categories> categories = categoryService.findAllCategories();
//        List<ProductFilters> filters = filterService.findAllFilters();
//        model.addAttribute("productCategory",productCategories);
////        model.addAttribute("productFilters",productFilters.);
//        model.addAttribute("category",categories);
//        model.addAttribute("brand",brands);
//        model.addAttribute("filter",filters);
//        model.addAttribute("product", product);
//        model.addAttribute("productBrand",productBrand);
//        return "admin/editProducts";
//    }
//    @PostMapping("/products/editProduct/edit/{id}")
//    public String productUpdate(
//        @PathVariable(name="id") Long productId,
//        @RequestParam(name="brandId",required = false) Long brandId,
//        @RequestParam(name="categoryId",required = false) Long categoryId,
//        @RequestParam(name="productName",required = false) String productName,
//        @RequestParam(name="description",required = false) String description,
//        @RequestParam(name="productImages",required = false) List<MultipartFile> imageFiles,
//        @RequestParam(name="productPrice",required = false) Float price,
//        @RequestParam(name="productStock",required = false) Integer stock,
//        @RequestParam(name="productFilters",required = false) List<ProductFilters> filters,
//        Model model,RedirectAttributes ra
//
//    ) throws IOException {
//        if(!productService.existsById(productId)){
//            throw new ResourceNotFoundException("Product not found");
//        }
//        Products productDetails = productService.findProductById(productId);
//        Brands brand = brandService.findBrandById(brandId);
//        Categories category = categoryService.findCatgeoryById(categoryId);
//        if(!productDetails.getProductName().equals(productName)||!productName.isEmpty()) {
//            productDetails.setProductName(productName);
//        }
//        Products tempProduct = productService.save(productDetails);
//        List<ProductImages> images = new ArrayList<>();
//        if(Objects.nonNull(imageFiles)) {
//            for (MultipartFile imageFile : imageFiles) {
//                String fileName = fileUploadDir(imageFile);
//                ProductImages imageObj = new ProductImages(fileName, tempProduct);
//                imageObj = productImageService.save(imageObj);
//                images.add(imageObj);
//            }
//        }
//        if(!images.isEmpty()) {
//            productDetails.setImages(images);
//        }
//        if(Objects.nonNull(description)) {
//            productDetails.setDescription(description);
//        }
//        if(!productDetails.getBrands().equals(brand)) {
//            productDetails.setBrands(brand);
//        }
//        if(!productDetails.getCategories().equals(category)) {
//            productDetails.setCategories(category);
//        }
//        if(!(productDetails.getStock() == stock)) {
//            productDetails.setStock(stock);
//        }
//            productDetails.setProductPrice(price);
//        if(Objects.nonNull(filters)) {
//            productDetails.setProductFilters(filters);
//        }
//        ra.addFlashAttribute("message","Product : "+productDetails.getProductName()+" is updated");
//        return "redirect:/admin/products";
//
//    }
//    @PostMapping("/products/deleteProduct/{id}")
//    public String deleteProduct(@PathVariable(name="id") Long ProductId,RedirectAttributes ra){
//        productService.deleteProduct(ProductId);
//        ra.addFlashAttribute("message","Product is deleted");
//        return "redirect:/admin/products";
//    }

    //Category APIs
//    @GetMapping("/categories")
//    public String addVariants(Model model,HttpServletRequest request){
//        model.addAttribute("requestURI",request.getRequestURI());
//        return "admin/addCategory";
//    }
//    @PostMapping("/categories/saveCategory")
//    public String saveVariants(
//        @RequestParam(name = "categoryName") String categoryName,
//        @RequestParam(name = "brandName") String brandName,
//        RedirectAttributes ra
//    ) {
//        if(!categoryName.isEmpty()){
//            if(categoryService.categoryExists(categoryName)){
//                ra.addFlashAttribute("message","Category already exists");
//                return "redirect:/admin/categories";
//            }
//            Categories categories = new Categories();
//            categories.setCategoryName(categoryName);
//            categoryService.saveCategory(categories);
//        }
//
//        if(!brandName.isEmpty()){
//            if(brandService.brandExists(brandName)){
//                ra.addFlashAttribute("message","Brand already exists");
//                return "redirect:/admin/categories";
//            }
//            Brands brands = new Brands();
//            brands.setBrandName(brandName);
//            brandService.saveBrand(brands);
//        }
//
//        ra.addFlashAttribute("message","Category Added to the list successfully");
//        return "redirect:/admin/categories";
//    }

    //Variant Controller Methods
//
//    @GetMapping("/variants")
//    public String variants(Model model, HttpServletRequest request){
//        model.addAttribute("requestURI",request.getRequestURI());
//        List<Categories> categories = categoryService.findAllCategories();
//        model.addAttribute("category",categories);
//        return "admin/addVariants";
//    }
//    @PostMapping("/variants/saveVariant")
//    public String addVariants(
//        @RequestParam(name="categoryId") Long categoryId,
//        @RequestParam(name="filterName") String filterName,
//        RedirectAttributes ra
//    ){
//        Categories category = categoryService.findCatgeoryById(categoryId);
//        if(filterService.variantExistInCategory(categoryId,filterName)){
//            ra.addFlashAttribute("message","Variant in that category is already present");
//            return "redirect:/admin/variants";
//        }
//        ProductFilters productFilters =  new ProductFilters();
//        productFilters.setCategory(category);
//        productFilters.setFilterName(filterName);
//        filterService.saveFilter(productFilters);
//        ra.addFlashAttribute("message","Your Variant is successfully added to the list");
//        return "redirect:/admin/variants";
//    }


//    // Image upload directory method
//    private String fileUploadDir(MultipartFile file) throws IOException {
//        String rootPath = System.getProperty("user.dir");
//        String uploadDir = rootPath + "/src/main/resources/static/uploads";
//
//        File dir = new File(uploadDir);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//
//        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
//
//        // Save the file to the upload directory
//        String filePath = uploadDir + "/" + fileName;
//        Path path = Paths.get(filePath);
//        System.out.println(path);
//        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//
//        // Return the file path
//        return fileName;
//    }


}


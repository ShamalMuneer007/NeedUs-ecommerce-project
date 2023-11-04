package com.needus.ecommerce.service.product.impl;

import com.needus.ecommerce.entity.product.*;
import com.needus.ecommerce.repository.product.ProductsRepository;
import com.needus.ecommerce.service.product.BrandService;
import com.needus.ecommerce.service.product.CategoryService;
import com.needus.ecommerce.service.product.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductsRepository repository;
    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Override
    public Products save(Products product) {
        product.setPublishedAt(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
        return repository.save(product);
    }


    @Override
    public Products findProductById(Long productId) {
        return repository.findById(productId).get();
    }

    @Override
    public void blockProduct(Long productId) {
        Products products = findProductById(productId);
        products.setProductStatus(!products.isProductStatus());
        repository.save(products);
    }

    @Override
    public Page<Products> findAllProducts(int pageNo,int pageSize) {
//        Sort sort = Sort.by(Sort.Order.desc("publishedAt"));
        Pageable pageable = PageRequest.of(pageNo-1,pageSize);
        return repository.findAllNonDeleted(pageable);
    }

    @Override
    public void deleteProduct(Long productId) {
        Products product = repository.findById(productId).get();
        product.setDeleted(true);
        repository.save(product);
    }

    @Override
    public Page<Products> findAllNonBlockedProducts(int pageNo, int pageSize) {
        Sort sort = Sort.by(Sort.Order.desc("publishedAt"));
        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize,sort);
        return repository.findByIsDeletedFalseAndProductStatusTrue(pageable);
    }
    @Override
    public List<Products> findAllNonBlockedProducts() {
        Sort sort = Sort.by(Sort.Order.desc("publishedAt"));
        return repository.findByIsDeletedFalseAndProductStatusTrue(sort);
    }

    @Override
    public boolean existsById(Long productId) {
        return repository.existsByProductIdAndIsDeletedFalse(productId);
    }

    @Override
    public Page<Products> findProductsOfCategory(Long categoryId,int pageNo,int pageSize) {
        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize);
        return repository.findByCategories_CategoryIdAndIsDeletedFalse(categoryId,pageable);
    }
    @Override
    public List<Products> findProductsOfCategory(Long categoryId) {
        return repository.findByCategories_CategoryIdAndIsDeletedFalse(categoryId);
    }

    @Override
    public void reduceStock(Long productId,int quantity) {
        Products products = repository.findById(productId).get();
        products.setStock(products.getStock()-quantity);
        repository.save(products);
    }

    @Override
    public Page<Products> findAllProductsWithtinParams(Long maxPrice, Long minPrice,
                                                       List<ProductFilters> productFilters, List<Brands> brands, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Products> products = repository.findAllNonDeletedWithinPriceRange(maxPrice, minPrice, pageable);
        if (Objects.isNull(productFilters) && Objects.isNull(brands)) {
            return products;
        }
        List<Products> brandFilteredProducts = new ArrayList<>();
        if (Objects.nonNull(brands)) {
            for (Products product : products) {
                for (Brands brand : brands) {
                    if (product.getBrands().equals(brand)) {
                        brandFilteredProducts.add(product);
                    }
                }
            }
        } else {
            brandFilteredProducts = products.stream().toList();
        }
        if (Objects.isNull(productFilters)) {
            return new PageImpl<>(brandFilteredProducts, pageable, brandFilteredProducts.size());
        } else {
            List<Products> filteredProducts = new ArrayList<>();
            for (Products product : brandFilteredProducts) {
                if (productFilters.stream().anyMatch(filters -> product.getProductFilters().contains(filters))) {
                    filteredProducts.add(product);
                }
            }
            return new PageImpl<>(filteredProducts, pageable, filteredProducts.size());
        }
    }

    @Override
    public Page<Products> searchProducts(int pageNo, int pageSize, String searchKey) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        List<Products> products = repository.searchAllNonBlockedAndNonDeletedProducts(searchKey);
        List<Categories> categories = categoryService.searchAllNonDeletedProductsBasedOnCategorySearchKey(searchKey);
        List<Brands> brands = brandService.searchAllNonDeletedProductsBasedOnBrandSearchKey(searchKey);
        List<Products> allProducts = findAllProducts();
        Set<Products> filteredProducts = allProducts.stream()
            .filter(product -> !products.contains(product) &&
                (categories.stream().anyMatch(category -> product.getCategories().equals(category)) ||
                    brands.stream().anyMatch(brand -> product.getBrands().equals(brand))))
            .collect(Collectors.toSet());
        products.addAll(filteredProducts);
        Set<Products> productResult = new HashSet<>(products);
        return new PageImpl<>(productResult.stream().toList(), pageable, products.size());
    }

    @Override
    public Page<Products> findProductBySearchKey(int pageNo, int pageSize,Long categoryId, String searchKey) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return repository.searchProductsInCategory(categoryId,searchKey,pageable);
    }

    @Override
    public List<Products> findAllProducts() {
        return repository.findByIsDeletedFalseAndProductStatusTrue();
    }

    @Override
    public void setProductAverageRating(Products product, int rating) {
        if(!product.getProductReview().isEmpty()){
            log.info("review size : "+product.getProductReview().size());
            log.info("given rating : "+rating);
            int totalRating;
            totalRating = product.getProductReview().stream().map(ProductReview::getRating).reduce(0,Integer::sum);
                log.info("Product total rating : "+totalRating);
                product.setAverageRating((totalRating)/(product.getProductReview().size()));
            repository.save(product);
        }

    }

    @Override
    public void deleteProductsOfCategory(Long categoryId) {
        List<Products> products =  repository.findByCategories_CategoryIdAndIsDeletedFalse(categoryId);
        products.forEach(product -> {
            product.setDeleted(true);
            repository.save(product);
        }
        );

    }

    @Override
    public void deleteProductsOfBrand(Long brandId) {
        List<Products> products = repository.findByBrands_BrandIdAndIsDeletedFalse(brandId);
        products.forEach(product -> {product.setDeleted(true);repository.save(product);});
    }

    @Override
    public void applyOfferForCategory(Long categoryId, Float discountPercentage) {
        List<Products> products = findProductsOfCategory(categoryId);
        products.forEach(product ->{
            product.setProductPrice(product.getProductBasePrice() - (product.getProductBasePrice()*(discountPercentage/100)));
            repository.save(product);
        });
    }

    @Override
    public void applyOfferForProduct(Long productId, Float discountPercentage, String expiryDate) {
        Products product = findProductById(productId);
        product.setProductPrice(product.getProductBasePrice()-(product.getProductBasePrice()*discountPercentage/100));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        product.setDiscountOfferExpired(false);
        product.setDiscountOfferExpiryDate(LocalDate.parse(expiryDate,formatter));
        repository.save(product);
    }

    @Override
    public List<Products> searchProducts(String keyword) {
        return repository.searchAllNonBlockedAndNonDeletedProducts(keyword);
    }

//    @EventListener(ApplicationReadyEvent.class)
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void checkProductOfferExpiration(){
//        List<Products> products = findAllProducts();
//        products.forEach(product -> {
//            if(!product.isDiscountOfferExpired()&&LocalDate.now().isAfter(product.getDiscountOfferExpiryDate())){
//                product.setProductPrice(product.getProductBasePrice());
//                product.setDiscountOfferExpired(true);
//                product.setDiscountOfferExpiryDate(null);
//        }
//        });
//        log.info("Product offer expiration validated");
//    }


}

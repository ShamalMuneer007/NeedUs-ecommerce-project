package com.needus.ecommerce.service.product.impl;

import com.needus.ecommerce.entity.product.Categories;
import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.repository.product.CategoryRepository;
import com.needus.ecommerce.repository.product.ProductsRepository;
import com.needus.ecommerce.service.product.CategoryService;
import com.needus.ecommerce.service.product.ProductService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Override
    public void saveCategory(Categories categories) {
        categoryRepository.save(categories);
    }

    @Override
    public Categories findCatgeoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).get();
    }

    @Override
    public List<Categories> findAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public boolean categoryExists(String categoryName) {
        if(categoryRepository.existsByCategoryNameAndIsDeletedIsFalse(categoryName)){
            if(categoryRepository.findByCategoryName(categoryName).getCategoryName().equalsIgnoreCase(categoryName)){
                return true;
            }
            return true;
        }
        return false;
    }

    @Override
    public void deleteCategoryById(Long categoryId)  {
        Categories categories = findCatgeoryById(categoryId);
        categories.setDeleted(true);
        categoryRepository.save(categories);
    }

    @Override
    public List<Categories> findAllNonDeletedCategories() {
        return categoryRepository.findByIsDeletedFalse();
    }

    @Override
    public List<Categories> searchAllNonDeletedProductsBasedOnCategorySearchKey(String searchKey) {
       return categoryRepository.searchAllNonDeletedCategories(searchKey);
    }

    @Override
    public void applyOffer(String expiryDate, Float discountPercentage, Long categoryId) {
        Categories category = categoryRepository.findById(categoryId).get();
        category.setDiscountOfferPercentage(discountPercentage);
        category.setDiscountOfferExpired(false);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        category.setDiscountOfferExpiryDate(LocalDate.parse(expiryDate,formatter));
    }
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 0 * * ?")
    public void expireCategoryOffers() {
        List<Categories> categories = findAllNonDeletedCategories();
        categories.forEach(this::expireCategoryOffer);
        log.info("Category offer expiration validated");
    }

    @Transactional
    public void expireCategoryOffer(Categories category) {
        if (!category.isDiscountOfferExpired() && LocalDate.now().isAfter(category.getDiscountOfferExpiryDate())) {
            resetProductPrices(category.getProducts());
            category.setDiscountOfferExpiryDate(null);
            category.setDiscountOfferExpired(true);
            category.setDiscountOfferPercentage(null);
            categoryRepository.save(category);
        }
    }

    @Transactional
    public void resetProductPrices(List<Products> products) {
        products.forEach(product -> {
            product.setProductPrice(product.getProductBasePrice());
            productsRepository.save(product);
        });
    }
}

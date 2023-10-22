package com.needus.ecommerce.service.product.impl;

import com.needus.ecommerce.entity.product.Categories;
import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.repository.product.CategoryRepository;
import com.needus.ecommerce.service.product.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
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
    public void deleteCategoryById(Long categoryId) {
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
}

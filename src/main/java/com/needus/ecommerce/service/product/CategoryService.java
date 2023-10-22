package com.needus.ecommerce.service.product;

import com.needus.ecommerce.entity.product.Categories;

import java.util.List;

public interface CategoryService {
    void saveCategory(Categories categories);

    Categories findCatgeoryById(Long categoryId);

    List<Categories> findAllCategories();

    boolean categoryExists(String categoryName);

    void deleteCategoryById(Long categoryId);

    List<Categories> findAllNonDeletedCategories();

    List<Categories> searchAllNonDeletedProductsBasedOnCategorySearchKey(String searchKey);
}

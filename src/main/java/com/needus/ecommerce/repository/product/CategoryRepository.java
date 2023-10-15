package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Categories,Long> {
    boolean existsByCategoryName(String categoryName);

    Categories findByCategoryName(String categoryName);

    List<Categories> findByIsDeletedFalse();

    boolean existsByCategoryNameAndIsDeletedIsFalse(String categoryName);
}

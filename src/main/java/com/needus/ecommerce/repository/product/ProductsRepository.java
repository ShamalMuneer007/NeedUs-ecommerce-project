package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Products,Long> {
    @Query(value = "SELECT * FROM Products WHERE is_deleted = false",nativeQuery = true)
    List<Products> findAllNonDeleted();

    List<Products> findByIsDeletedFalseAndProductStatusTrue();

    List<Products> findByCategories_CategoryIdAndIsDeletedFalse(Long categoryId);
}

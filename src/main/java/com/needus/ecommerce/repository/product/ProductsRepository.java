package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Products,Long> {
    @Query(value = "SELECT * FROM products WHERE is_deleted = false",nativeQuery = true)
    List<Products> findAllNonDeleted();

    Page<Products> findByIsDeletedFalseAndProductStatusTrue(Pageable pageable);

    Page<Products> findByCategories_CategoryIdAndIsDeletedFalse(Long categoryId,Pageable pageable);

    @Query(value = "SELECT * FROM products WHERE is_deleted = false" +
        " AND product_status = true" +
        " AND product_price BETWEEN ?2 AND ?1",nativeQuery = true)
    Page<Products> findAllNonDeletedWithinPriceRange(Long maxPrice,Long minPrice,Pageable pageable);

    @Query(value = "SELECT * FROM products " +
        " WHERE is_deleted = false" +
        " AND product_status = true" +
        " AND product_name LIKE %:searchKey%",
        nativeQuery = true)
    Page<Products> searchAllNonBlockedAndNonDeletedProducts(@Param("searchKey")String searchKey, Pageable pageable);
}

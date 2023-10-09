package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.ProductFilters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductFilterRepository extends JpaRepository<ProductFilters,Integer> {
    ProductFilters findByCategory_CategoryIdAndFilterName(Long categoryId, String filterName);

    @Query(value = "SELECT * FROM product_filters WHERE category_id = ?1",nativeQuery = true)
    List<ProductFilters> findForCategory(Long categoryId);
}

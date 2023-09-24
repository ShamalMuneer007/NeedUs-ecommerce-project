package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.ProductFilters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductFilterRepository extends JpaRepository<ProductFilters,Integer> {
}

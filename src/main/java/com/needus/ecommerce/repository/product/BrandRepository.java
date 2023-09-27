package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brands,Long> {
    boolean existsByBrandName(String brandName);

    Brands findByBrandName(String brandName);
}

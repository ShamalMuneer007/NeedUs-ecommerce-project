package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImages,Long> {
}

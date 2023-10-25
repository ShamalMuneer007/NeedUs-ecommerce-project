package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReviewRepository extends JpaRepository<ProductReview,Long> {
}

package com.needus.ecommerce.service.product;

import com.needus.ecommerce.entity.product.ProductReview;
import com.needus.ecommerce.entity.product.Products;

import java.util.List;

public interface ProductReviewService {
    ProductReview saveReview(ProductReview productReview);

    List<ProductReview> findAllProductReviews(Products product);
}

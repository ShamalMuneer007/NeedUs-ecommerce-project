package com.needus.ecommerce.service.product.impl;

import com.needus.ecommerce.entity.product.ProductReview;
import com.needus.ecommerce.repository.product.ProductReviewRepository;
import com.needus.ecommerce.service.product.ProductReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductReviewServiceImpl implements ProductReviewService {

    @Autowired
    private ProductReviewRepository reviewRepository;
    @Override
    public ProductReview saveReview(ProductReview productReview) {
        return reviewRepository.save(productReview);
    }
}

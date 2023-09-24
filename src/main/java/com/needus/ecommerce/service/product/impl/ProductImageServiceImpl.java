package com.needus.ecommerce.service.product.impl;

import com.needus.ecommerce.entity.product.ProductImages;
import com.needus.ecommerce.repository.product.ProductImageRepository;
import com.needus.ecommerce.service.product.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductImageServiceImpl implements ProductImageService {
    @Autowired
    ProductImageRepository repository;
    @Override
    public ProductImages save(ProductImages productImage) {
        return repository.save(productImage);
    }
}

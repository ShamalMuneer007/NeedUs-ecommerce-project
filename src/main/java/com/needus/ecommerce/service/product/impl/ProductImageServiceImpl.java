package com.needus.ecommerce.service.product.impl;

import com.needus.ecommerce.entity.product.ProductImages;
import com.needus.ecommerce.repository.product.ProductImageRepository;
import com.needus.ecommerce.service.product.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductImageServiceImpl implements ProductImageService {
    @Autowired
    ProductImageRepository repository;
    @Override
    public ProductImages save(ProductImages productImage) {
        return repository.save(productImage);
    }

    @Override
    public boolean existsById(Long imageId) {
        return repository.existsById(imageId);
    }

    @Override
    public ProductImages findImageById(Long imageId) {
        return repository.findById(imageId).get();
    }

    @Override
    public void removeProductImages(List<ProductImages> imagesList) {
        repository.deleteAllInBatch(imagesList);
    }

    @Override
    public void removeProductImageById(Long imageId) {
        repository.deleteById(imageId);
    }
}

package com.needus.ecommerce.service.product.impl;

import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.repository.product.ProductsRepository;
import com.needus.ecommerce.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductsRepository repository;
    @Override
    public Products save(Products product) {
        product.setPublishedAt(LocalDate.now());
        return repository.save(product);
    }

    @Override
    public List<Products> findAllProducts() {
        return repository.findAll();
    }
}

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
    public Products findProductById(Long productId) {
        return repository.findById(productId).get();
    }

    @Override
    public void blockProduct(Long productId) {
        Products products = findProductById(productId);
        products.setProductStatus(!products.isProductStatus());
        repository.save(products);
    }

    @Override
    public List<Products> findAllProducts() {
        return repository.findAllNonDeleted();
    }

    @Override
    public void deleteProduct(Long productId) {
        Products product = repository.findById(productId).get();
        product.setDeleted(true);
        repository.save(product);
    }

    @Override
    public List<Products> findAllNonBlockedProducts() {
        return repository.findByIsDeletedFalseAndProductStatusTrue();
    }

    @Override
    public boolean existsById(Long productId) {
        return repository.existsById(productId);
    }

    @Override
    public List<Products> findProductsOfCategory(Long categoryId) {
        return repository.findByCategories_CategoryIdAndIsDeletedFalse(categoryId);
    }

    @Override
    public void reduceStock(Long productId,int quantity) {
        Products products = repository.findById(productId).get();
        products.setStock(products.getStock()-quantity);
        repository.save(products);
    }

}

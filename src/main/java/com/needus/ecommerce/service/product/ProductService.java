package com.needus.ecommerce.service.product;

import com.needus.ecommerce.entity.product.Products;

import java.util.List;

public interface ProductService {
    public Products save(Products product);

    public List<Products> findAllProducts();
}

package com.needus.ecommerce.service.product;

import com.needus.ecommerce.entity.product.Products;

import java.util.List;

public interface ProductService {
    public Products save(Products product);



    public Products findProductById(Long productId);

    public void blockProduct(Long productId);

    public List<Products> findAllProducts();

    void deleteProduct(Long productId);

    List<Products> findAllNonBlockedProducts();

    boolean existsById(Long productId);

    List<Products> findProductsOfCategory(Long categoryId);

    void reduceStock(Long productId,int quantity);
}

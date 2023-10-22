package com.needus.ecommerce.service.product;

import com.needus.ecommerce.entity.product.Brands;
import com.needus.ecommerce.entity.product.ProductFilters;
import com.needus.ecommerce.entity.product.Products;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    public Products save(Products product);



    public Products findProductById(Long productId);

    public void blockProduct(Long productId);

    public Page<Products> findAllProducts(int pageNo,int pageSize);

    void deleteProduct(Long productId);

    Page<Products> findAllNonBlockedProducts(int pageNo, int pageSize);

    boolean existsById(Long productId);

    Page<Products> findProductsOfCategory(Long categoryId, int pageNo, int pageSize);

    void reduceStock(Long productId,int quantity);

    Page<Products> findAllProductsWithtinParams(Long maxPrice, Long minPrice,
                                                List<ProductFilters> productFilters, List<Brands> brands, int pageNo, int pageSize);

    Page<Products> searchProducts(int pageNo, int pageSize, String searchKey);

    Page<Products> findProductBySearchKey(int pageNo, int pageSize, Long categoryId, String searchKey);

    List<Products> findAllProducts();
}

package com.needus.ecommerce.service.product.impl;

import com.needus.ecommerce.entity.product.Brands;
import com.needus.ecommerce.entity.product.ProductFilters;
import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.repository.product.ProductsRepository;
import com.needus.ecommerce.service.product.CategoryService;
import com.needus.ecommerce.service.product.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductsRepository repository;
    @Autowired
    CategoryService categoryService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Override
    public Products save(Products product) {
        product.setPublishedAt(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
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
    public Page<Products> findAllProducts(int pageNo,int pageSize) {
        Sort sort = Sort.by(Sort.Order.desc("publishedAt"));
        Pageable pageable = PageRequest.of(pageNo-1,pageSize,sort);
        return repository.findAllNonDeleted(pageable);
    }

    @Override
    public void deleteProduct(Long productId) {
        Products product = repository.findById(productId).get();
        product.setDeleted(true);
        repository.save(product);
    }

    @Override
    public Page<Products> findAllNonBlockedProducts(int pageNo, int pageSize) {
        Sort sort = Sort.by(Sort.Order.desc("publishedAt"));
        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize,sort);
        return repository.findByIsDeletedFalseAndProductStatusTrue(pageable);
    }

    @Override
    public boolean existsById(Long productId) {
        return repository.existsById(productId);
    }

    @Override
    public Page<Products> findProductsOfCategory(Long categoryId,int pageNo,int pageSize) {
        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize);
        return repository.findByCategories_CategoryIdAndIsDeletedFalse(categoryId,pageable);
    }

    @Override
    public void reduceStock(Long productId,int quantity) {
        Products products = repository.findById(productId).get();
        products.setStock(products.getStock()-quantity);
        repository.save(products);
    }

    @Override
    public Page<Products> findAllProductsWithtinParams(Long maxPrice, Long minPrice,
                                                       List<ProductFilters> productFilters, List<Brands> brands, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<Products> products = repository.findAllNonDeletedWithinPriceRange(maxPrice, minPrice, pageable);
        if (Objects.isNull(productFilters) && Objects.isNull(brands)) {
            return products;
        }
        List<Products> brandFilteredProducts = new ArrayList<>();
        if (Objects.nonNull(brands)) {
            for (Products product : products) {
                for (Brands brand : brands) {
                    if (product.getBrands().equals(brand)) {
                        brandFilteredProducts.add(product);
                    }
                }
            }
        } else {
            brandFilteredProducts = products.stream().toList();
        }
        if (Objects.isNull(productFilters)) {
            return new PageImpl<>(brandFilteredProducts, pageable, brandFilteredProducts.size());
        } else {
            List<Products> filteredProducts = new ArrayList<>();
            for (Products product : brandFilteredProducts) {
                if (productFilters.stream().anyMatch(filters -> product.getProductFilters().contains(filters))) {
                    filteredProducts.add(product);
                }
            }
            return new PageImpl<>(filteredProducts, pageable, filteredProducts.size());
        }
    }

    @Override
    public Page<Products> searchProducts(int pageNo, int pageSize, String searchKey) {
//        Sort sort = Sort.by(Sort.Order.desc("publishedAt"));
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return repository.searchAllNonBlockedAndNonDeletedProducts(searchKey,pageable);
    }

    @Override
    public Page<Products> findProductBySearchKey(int pageNo, int pageSize,Long categoryId, String searchKey) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return repository.searchProductsInCategory(categoryId,searchKey,pageable);
    }

}

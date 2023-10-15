package com.needus.ecommerce.service.product;

import com.needus.ecommerce.entity.product.Brands;

import java.util.List;

public interface BrandService {
    void saveBrand(Brands brands);

    List<Brands> findAllBrands();

    Brands findBrandById(Long brandId);

    boolean brandExists(String brandName);

    void deleteBrandById(Long brandId);

    List<Brands> findAllNonDeletedBrands();
}

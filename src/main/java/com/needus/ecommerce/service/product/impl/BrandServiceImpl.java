package com.needus.ecommerce.service.product.impl;

import com.needus.ecommerce.entity.product.Brands;
import com.needus.ecommerce.repository.product.BrandRepository;
import com.needus.ecommerce.service.product.BrandService;
import com.needus.ecommerce.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandRepository brandRepository;



    @Override
    public void saveBrand(Brands brands) {
        brandRepository.save(brands);
    }

    @Override
    public List<Brands> findAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public Brands findBrandById(Long brandId) {
        return brandRepository.findById(brandId).get();
    }

    @Override
    public boolean brandExists(String brandName) {
        Brands brands = brandRepository.findByBrandName(brandName);
        if(brandRepository.existsByBrandNameAndIsDeletedIsFalse(brandName)){
            if(brands.getBrandName().equalsIgnoreCase(brandName)){
                return true;
            }
            return true;
        }
        return false;
    }

    @Override
    public void deleteBrandById(Long brandId) {
        Brands brands = findBrandById(brandId);
        brands.setDeleted(true);
        brandRepository.save(brands);
    }

    @Override
    public List<Brands> findAllNonDeletedBrands() {
        return brandRepository.findByIsDeletedFalse();
    }

    @Override
    public List<Brands> searchAllNonDeletedProductsBasedOnBrandSearchKey(String searchKey) {
        return brandRepository.searchAllNonDeletedBrands(searchKey);
    }
}

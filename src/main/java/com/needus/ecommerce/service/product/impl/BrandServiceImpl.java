package com.needus.ecommerce.service.product.impl;

import com.needus.ecommerce.entity.product.Brands;
import com.needus.ecommerce.repository.product.BrandRepository;
import com.needus.ecommerce.service.product.BrandService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

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
}

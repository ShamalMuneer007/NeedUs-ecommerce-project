package com.needus.ecommerce.service.product.impl;

import com.needus.ecommerce.entity.product.ProductFilters;
import com.needus.ecommerce.repository.product.ProductFilterRepository;
import com.needus.ecommerce.service.product.ProductFilterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductFilterServiceImpl implements ProductFilterService {
    private final ProductFilterRepository filterRepository;

    public ProductFilterServiceImpl(ProductFilterRepository filterRepository) {
        this.filterRepository = filterRepository;
    }

    @Override
    public boolean variantExistInCategory(Long categoryId , String filterName)
    {
        ProductFilters existingVariant =
            filterRepository.findByCategory_CategoryIdAndFilterName(categoryId, filterName);
        return existingVariant!=null;
    }

    @Override
    public ProductFilters saveFilter(ProductFilters filters) {
        return filterRepository.save(filters);
    }

    @Override
    public List<ProductFilters> findAllFilters() {
        return filterRepository.findAll();
    }
}

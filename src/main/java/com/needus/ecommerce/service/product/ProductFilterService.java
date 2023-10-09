package com.needus.ecommerce.service.product;

import com.needus.ecommerce.entity.product.ProductFilters;

import java.util.List;

public interface ProductFilterService {
    public boolean variantExistInCategory(Long categoryId, String filterName);
    public ProductFilters saveFilter(ProductFilters filters);

    public List<ProductFilters> findAllFilters();

    List<ProductFilters> findAllFiltersForCategory(Long categoryId);
}

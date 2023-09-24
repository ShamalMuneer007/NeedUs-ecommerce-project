package com.needus.ecommerce.service.product;

import com.needus.ecommerce.entity.product.ProductFilters;

import java.util.List;

public interface ProductFilterService {
    public void saveFilter(ProductFilters filters);

    public List<ProductFilters> findAllFilters();
}

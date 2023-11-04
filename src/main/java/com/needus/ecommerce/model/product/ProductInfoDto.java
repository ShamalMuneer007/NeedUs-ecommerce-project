package com.needus.ecommerce.model.product;

public class ProductInfoDto {
    public String productName;
    public Long productId;

    public ProductInfoDto(String productName, Long productId) {
        this.productName = productName;
        this.productId = productId;
    }
}

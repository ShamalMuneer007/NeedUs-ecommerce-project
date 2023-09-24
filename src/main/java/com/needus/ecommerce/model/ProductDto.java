package com.needus.ecommerce.model;

import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.service.product.ProductService;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProductDto {
    private String productName;
    private UserInformation userInformation;
    private LocalDate publishedAt;
    private String description;
    private Float productPrice;
    private boolean productStatus;
    private int stock;
    private int averageRating;
    private String sellerName;
    public ProductDto(Products product) {
            this.userInformation = product.getUserInformation();
            this.averageRating = product.getAverageRating();
            this.productPrice = product.getProductPrice();
            this.description = product.getDescription();
            this.productName = product.getProductName();
            this.stock = product.getStock();
            this.publishedAt = product.getPublishedAt();
            this.productStatus = product.isProductStatus();
            this.sellerName = userInformation.getUsername();
    }



}

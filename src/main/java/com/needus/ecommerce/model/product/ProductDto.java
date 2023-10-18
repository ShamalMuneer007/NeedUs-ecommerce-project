package com.needus.ecommerce.model.product;

import com.needus.ecommerce.entity.product.ProductImages;
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
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDto {
    private Long productId;
    private String productName;
    private UserInformation userInformation;
    private LocalDateTime publishedAt;
    private String description;
    private Float productPrice;
    private boolean productStatus;
    private int stock;
    private int averageRating;
    private String sellerName;
    private String ImageFilePath;
    public ProductDto(Products product) {
            this.productId = product.getProductId();
            this.userInformation = product.getUserInformation();
            this.averageRating = product.getAverageRating();
            this.productPrice = product.getProductPrice();
            this.description = product.getDescription();
            this.productName = product.getProductName();
            this.stock = product.getStock();
            this.publishedAt = product.getPublishedAt();
            this.productStatus = product.isProductStatus();
            this.sellerName = userInformation.getUsername();
            this.setImageFilePath("/uploads/"+product.getImages().get(0).getFileName());
    }



}

package com.needus.ecommerce.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long image_id;
    private String fileName;
    @ManyToOne
    @JoinColumn(name = "productId") // This should match the actual column name in the database
    private Products product;

    public ProductImages(String fileName, Products tempProduct) {
        this.fileName = fileName;
        this.product= tempProduct;
    }
}

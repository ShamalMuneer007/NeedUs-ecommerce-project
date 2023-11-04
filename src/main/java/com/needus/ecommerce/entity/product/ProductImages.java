package com.needus.ecommerce.entity.product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long image_id;
    private String fileName;
    @ManyToOne
    @JoinColumn(name = "productId")
    private Products product;

    public ProductImages(String fileName, Products tempProduct) {
        this.fileName = fileName;
        this.product= tempProduct;
    }
}

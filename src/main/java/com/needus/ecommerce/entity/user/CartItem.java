package com.needus.ecommerce.entity.user;

import com.needus.ecommerce.entity.product.Coupon;
import com.needus.ecommerce.entity.product.Products;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "product")
    private Products product;
    private int quantity;
}

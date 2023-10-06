package com.needus.ecommerce.entity.user.order;

import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user.CartItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private Products product;
    private int quantity;
    public OrderItem(CartItem cartItem){
        this.product = cartItem.getProduct();
        this.quantity = cartItem.getQuantity();
    }
}

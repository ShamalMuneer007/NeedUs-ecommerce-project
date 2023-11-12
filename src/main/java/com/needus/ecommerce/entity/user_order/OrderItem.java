package com.needus.ecommerce.entity.user_order;

import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user.CartItem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Products product;
    private int quantity;
    public OrderItem(CartItem cartItem){
        this.product = cartItem.getProduct();
        this.quantity = cartItem.getQuantity();
    }
}

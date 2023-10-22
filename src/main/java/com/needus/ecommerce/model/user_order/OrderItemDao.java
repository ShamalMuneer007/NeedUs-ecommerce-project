package com.needus.ecommerce.model.user_order;

import com.needus.ecommerce.entity.product.Products;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDao {
    private Products product;
    private Integer quantity;
}

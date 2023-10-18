package com.needus.ecommerce.model.user;

import com.needus.ecommerce.entity.product.Products;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private long cartId;
    private List<Products> productsList;
}

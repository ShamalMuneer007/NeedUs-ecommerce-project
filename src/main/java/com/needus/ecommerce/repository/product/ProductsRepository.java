package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsRepository extends JpaRepository<Products,Long> {
}

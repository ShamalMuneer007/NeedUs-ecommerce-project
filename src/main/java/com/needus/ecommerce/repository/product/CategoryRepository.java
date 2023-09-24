package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Categories,Long> {
}

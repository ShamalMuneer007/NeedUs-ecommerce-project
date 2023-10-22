package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Categories,Long> {
    boolean existsByCategoryName(String categoryName);

    Categories findByCategoryName(String categoryName);

    List<Categories> findByIsDeletedFalse();

    boolean existsByCategoryNameAndIsDeletedIsFalse(String categoryName);

    @Query(value = "SELECT * FROM categories"+
        " WHERE is_deleted = false " +
        " AND category_name LIKE %:searchKey%",nativeQuery = true)
    List<Categories> searchAllNonDeletedCategories(@Param("searchKey") String searchKey);
}

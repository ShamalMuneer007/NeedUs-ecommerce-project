package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brands,Long> {
    boolean existsByBrandName(String brandName);

    Brands findByBrandName(String brandName);

    List<Brands> findByIsDeletedFalse();

    boolean existsByBrandNameAndIsDeletedIsFalse(String brandName);

    @Query(value = "SELECT * FROM brands"+
        " WHERE is_deleted = false " +
        " AND brand_name LIKE %:searchKey%",nativeQuery = true)
    List<Brands> searchAllNonDeletedBrands(@Param("searchKey") String searchKey);

}

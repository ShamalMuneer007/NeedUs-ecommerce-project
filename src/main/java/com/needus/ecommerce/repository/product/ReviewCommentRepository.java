package com.needus.ecommerce.repository.product;

import com.needus.ecommerce.entity.product.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment,Long> {
}

package com.needus.ecommerce.repository.user;

import com.needus.ecommerce.entity.user_order.OrderItem;
import com.needus.ecommerce.model.user_order.OrderItemDao;
import com.needus.ecommerce.model.user_order.OrderItemProjection;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//@SqlResultSetMapping(
//    name = "OrderItemMapping",
//    classes = @ConstructorResult(
//        targetClass = OrderItemDao.class,
//        columns = {
//            @ColumnResult(name = "id", type = Long.class),
//            @ColumnResult(name = "productId", type = Long.class),
//            @ColumnResult(name = "quantity", type = Integer.class)
//        }
//    )
//)
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    @Query(value = "SELECT product_product_id AS productId, SUM(quantity) AS quantity " +
        "FROM order_item " +
        "JOIN user_order_order_items ON user_order_order_items.order_items_id = order_item.id " +
        "JOIN user_order ON user_order_order_items.user_order_order_id = user_order.order_id " +
        "WHERE user_order.order_status = 'DELIVERED' " +
        "GROUP BY product_product_id", nativeQuery = true)
    List<OrderItemProjection> findAllGroupByProducts();
}

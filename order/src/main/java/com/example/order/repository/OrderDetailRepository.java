package com.example.order.repository;

import com.example.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID>, JpaSpecificationExecutor<OrderDetail> {
    @Query("select o from OrderDetail o WHERE o.id = :idOrderDetail")
    public OrderDetail getById(@Param("idOrderDetail") UUID idOrderDetail);
}

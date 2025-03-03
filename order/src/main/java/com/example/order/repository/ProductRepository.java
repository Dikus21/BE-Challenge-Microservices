package com.example.order.repository;

import com.example.order.entity.Order;
import com.example.order.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    @Query("select p from Product p WHERE p.id = :idProduct")
    public Product getById(@Param("idProduct") UUID idProduct);

    @Query("select p from Product p WHERE p.id = :idProduct and p.merchantId = :merchantId")
    public Product getByIdAndMerchantId(@Param("idProduct") UUID idProduct, @Param("merchantId") UUID merchantId);
}

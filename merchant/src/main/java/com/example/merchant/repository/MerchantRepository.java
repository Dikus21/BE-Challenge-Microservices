package com.example.merchant.repository;

import com.example.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, UUID>, JpaSpecificationExecutor<Merchant> {
    @Query("select m from Merchant m WHERE m.id = :idMerchant")
    public Merchant getById(@Param("idMerchant") UUID idMerchant);

    @Query("select m from Merchant m WHERE m.userId = :idUser")
    public Merchant getByUserId(@Param("idUser") Long idUser);

    @Query(value = "select count(m) from Merchant m WHERE m.name = :merchantName")
    public Long getByName(@Param("merchantName") String merchantName);

    @Query(value = "SELECT SUM(od.total_price) " +
            "FROM order_detail od " +
            "JOIN product p ON od.product_id = p.id " +
            "JOIN orders o ON od.order_id = o.id " +
            "JOIN merchant m ON p.merchant_id = m.id " +
            "WHERE m.id = :merchantId " +
            "AND o.order_time BETWEEN :startDate AND :endDate ", nativeQuery = true)
    BigDecimal sumIncomeMerchant(@Param("merchantId") UUID merchantId,
                                 @Param("startDate") Date startDate,
                                 @Param("endDate") Date endDate);
//    @Query(value = "SELECT SUM(od.total_price) " +
//            "FROM order_detail od " +
//            "JOIN product p ON od.product_id = p.id " +
//            "JOIN orders o ON od.order_id = o.id " +
//            "JOIN merchant m ON p.merchant_id = m.id " +
//            "WHERE m.id = :merchantId " +
//            "AND o.order_date BETWEEN :startDate AND :endDate ", nativeQuery = true)
//    int sumPotentialIncomeMerchant(@Param("merchantId") UUID merchantId,
//                              @Param("startDate") Date startDate,
//                              @Param("endDate") Date endDate);

}

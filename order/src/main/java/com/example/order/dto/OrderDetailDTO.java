package com.example.order.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class OrderDetailDTO {
    private String detailId;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private BigDecimal totalPrice;
    private BigDecimal totalBill;

}

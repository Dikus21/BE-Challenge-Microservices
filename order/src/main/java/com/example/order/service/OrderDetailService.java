package com.example.order.service;


import com.example.order.entity.OrderDetail;

import java.util.Map;
import java.util.UUID;

public interface OrderDetailService {
    Map<Object, Object> save(OrderDetail request);

    Map<Object, Object> update(OrderDetail request);

    Map<Object, Object> delete(OrderDetail request);

    Map<Object, Object> getById(UUID uuid);

//    BigDecimal totalPrice(OrderDetail request);
}

package com.example.order.service;

import com.example.order.entity.Order;

import java.util.Map;
import java.util.UUID;

public interface OrderService {
    Map<Object, Object> save(Order request);

    Map<Object, Object> update(Order request);

    Map<Object, Object> delete(Order request);

    Map<Object, Object> getById(UUID uuid);

    Map<Object, Object> getOrderDetailList(Order request);
}

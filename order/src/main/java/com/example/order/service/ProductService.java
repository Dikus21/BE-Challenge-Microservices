package com.example.order.service;

import com.example.order.entity.Product;

import java.util.Map;
import java.util.UUID;

public interface ProductService {
    Map<Object, Object> save(Product request);

    Map<Object, Object> update(Product request);

    Map<Object, Object> delete(Product request);

    Map<Object, Object> getById(UUID uuid);
}

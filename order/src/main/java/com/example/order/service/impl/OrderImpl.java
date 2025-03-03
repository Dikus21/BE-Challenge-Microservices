package com.example.order.service.impl;

import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import com.example.order.service.OrderService;
import com.example.order.utils.TemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class OrderImpl implements OrderService {
    @Autowired
    public OrderRepository orderRepository;
    @Autowired
    public TemplateResponse templateResponse;

    @Override
    public Map<Object, Object> save(Order request) {
        try {
            log.info("Save New Order");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            Long userId = (Long) attribute.getRequest().getAttribute("userId");
            if (request.getDestinationAddress().isEmpty()) return templateResponse.error("Destination Address is required");

            log.info("Order Save Success");
            request.setUserId(userId);
            return templateResponse.success(orderRepository.save(request));
        } catch (Exception e) {
            log.error("Save Order Error: " + e.getMessage());
            return templateResponse.error("Save Order: " + e.getMessage());
        }
    }

    @Override
    public Map<Object, Object> update(Order request) {
        try {
            log.info("Update Order");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            Long userId = (Long) attribute.getRequest().getAttribute("userId");
            if (request.getId() == null) return templateResponse.error("User id is null");

            Order order = orderRepository.getByIdAndUserId(request.getId(), userId);
            if (order == null)return templateResponse.error("order not found");
            if (!request.getDestinationAddress().isEmpty()) order.setDestinationAddress(request.getDestinationAddress());
            order.setCompleted(request.getCompleted());

            log.info("Update Order Success");
            return templateResponse.success(orderRepository.save(order));
        } catch (Exception e) {
            log.error("Update Order Error: " + e.getMessage());
            return templateResponse.error("Update Order: " + e.getMessage());
        }
    }

    @Override
    public Map<Object, Object> delete(Order request) {
        try {
            log.info("Delete Order");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            Long userId = (Long) attribute.getRequest().getAttribute("userId");
            Order order = orderRepository.getByIdAndUserId(request.getId(), userId);
            if (order == null)return templateResponse.error("order not found");

            log.info("Order Deleted");
            order.setDeletedDate(new Date());
            return templateResponse.success(orderRepository.save(order));
        } catch (Exception e) {
            log.error("Delete Order Error: " + e.getMessage());
            return templateResponse.error("Delete Order : " + e.getMessage());
        }
    }

    @Override
    public Map<Object, Object> getById(UUID uuid) {
        try {
            log.info("Get Order");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            Long userId = (Long) attribute.getRequest().getAttribute("userId");

            if (uuid == null) return templateResponse.error("Id is required");
            Order order = orderRepository.getByIdAndUserId(uuid, userId);
            if (order == null)return templateResponse.error("order not found");

            log.info("Order Found");
            return templateResponse.success(order);
        } catch (Exception e) {
            log.error("Get Order Error: " + e.getMessage());
            return templateResponse.error("Get Order: " + e.getMessage());
        }
    }

    @Override
    public Map<Object, Object> getOrderDetailList(Order request) {
        try {
            log.info("Get Order Detail List");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            Long userId = (Long) attribute.getRequest().getAttribute("userId");
            if (request.getId() ==  null) return templateResponse.error("Id is required");
            Order order = orderRepository.getByIdAndUserId(request.getId(), userId);
            if (order == null) return templateResponse.error("Order not Found");

            log.info("Get Orders Detail List Success");
            return templateResponse.success(order);
        } catch (Exception e) {
            log.error("Get Order Detail List Error: " + e.getMessage());
            return templateResponse.error("Get Order Detail List: " + e.getMessage());
        }
    }
}

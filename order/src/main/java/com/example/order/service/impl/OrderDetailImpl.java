package com.example.order.service.impl;

import com.example.order.entity.Order;
import com.example.order.entity.OrderDetail;
import com.example.order.entity.Product;
import com.example.order.repository.OrderDetailRepository;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.ProductRepository;
import com.example.order.service.OrderDetailService;
import com.example.order.utils.TemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class OrderDetailImpl implements OrderDetailService {
    @Autowired
    public OrderDetailRepository orderDetailRepository;
    @Autowired
    public TemplateResponse templateResponse;
    @Autowired
    public OrderRepository orderRepository;
    @Autowired
    public ProductRepository productRepository;

    @Override
    public Map<Object, Object> save(OrderDetail request) {
        try {
            log.info("Save New Order Detail");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            Long userId = (Long) attribute.getRequest().getAttribute("userId");
            if (request.getOrder().getId() == null) return templateResponse.error("Order Id is Required");
            Order order = orderRepository.getByIdAndUserId(request.getOrder().getId(), userId);
            if (order == null) return templateResponse.error("Order Not Found");
            request.setOrder(order);
            Optional<Product> checkDataDBProduct = productRepository.findById(request.getProduct().getId());
            if (!checkDataDBProduct.isPresent()) return templateResponse.error("Product Not Found");
            request.setProduct(checkDataDBProduct.get());
            if (request.getQuantity() == 0) return templateResponse.error("Quantity is required");
            request.setTotalPrice(request.getProduct().getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));

            log.info("Order Detail Save Success");
            return templateResponse.success(orderDetailRepository.save(request));
        } catch (Exception e) {
            log.error("Save Order Detail Error: " + e.getMessage());
            return templateResponse.error("Save Order Detail: " + e.getMessage());
        }
    }

    @Override
    public Map<Object, Object> update(OrderDetail request) {
        try {
            log.info("Update Order Detail");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            Long userId = (Long) attribute.getRequest().getAttribute("userId");

            if (request.getId() == null) return templateResponse.error("Id is required");
            Optional<OrderDetail> checkDataDBOrderDetail = orderDetailRepository.findById(request.getId());
            Order order = orderRepository.getByIdAndUserId(checkDataDBOrderDetail.get().getOrder().getId(), userId);
            if (order == null) return templateResponse.error("Order Detail not Found");
            if (request.getQuantity() == 0) delete(checkDataDBOrderDetail.get());
            checkDataDBOrderDetail.get().setTotalPrice(request.getProduct().getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));


            log.info("Update Order Detail Success");
            return templateResponse.success(orderDetailRepository.save(checkDataDBOrderDetail.get()));
        } catch (Exception e) {
            log.error("Update Order Detail Error: " + e.getMessage());
            return templateResponse.error("Update Order Detail: " + e.getMessage());
        }
    }

    @Override
    public Map<Object, Object> delete(OrderDetail request) {
        try {
            log.info("Delete Order Detail");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            Long userId = (Long) attribute.getRequest().getAttribute("userId");

            if (request.getId() == null) return templateResponse.error("Id is required");
            Optional<OrderDetail> checkDataDBOrderDetail = orderDetailRepository.findById(request.getId());
            Order order = orderRepository.getByIdAndUserId(checkDataDBOrderDetail.get().getOrder().getId(), userId);
            if (order == null) return templateResponse.error("Order Detail not Found");

            log.info("Order Deleted");
            checkDataDBOrderDetail.get().setDeletedDate(new Date());
            return templateResponse.success(orderDetailRepository.save(checkDataDBOrderDetail.get()));
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
            Optional<OrderDetail> checkDataDBOrderDetail = orderDetailRepository.findById(uuid);
            Order order = orderRepository.getByIdAndUserId(checkDataDBOrderDetail.get().getOrder().getId(), userId);
            if (order == null) return templateResponse.error("Order Detail not Found");

            log.info("Order Found");
            return templateResponse.success(checkDataDBOrderDetail.get());
        } catch (Exception e) {
            log.error("Get Order Error: " + e.getMessage());
            return templateResponse.error("Get Order: " + e.getMessage());
        }
    }


//    @Override
//    public BigDecimal totalPrice(OrderDetail orderDetail) {
//        return orderDetail.getProduct().getPrice().multiply(orderDetail.getQuantity());
//    }


}

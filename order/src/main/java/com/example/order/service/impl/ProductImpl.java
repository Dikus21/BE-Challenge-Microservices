package com.example.order.service.impl;

import com.example.order.entity.Product;
import com.example.order.repository.ProductRepository;
import com.example.order.service.ProductService;
import com.example.order.utils.TemplateResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ProductImpl implements ProductService {
    @Autowired
    public ProductRepository productRepository;
    @Autowired
    public TemplateResponse templateResponse;

    @Override
    public Map<Object, Object> save(Product request) {
        try {
            log.info("Save New Product");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            UUID merchantId = (UUID) attribute.getRequest().getAttribute("merchantId");
            if (merchantId == null) return templateResponse.error("merchant id not found, please register as merchant");
            request.setMerchantId(merchantId);

            if (request.getName().isEmpty()) return templateResponse.error("Name is required");
            if (request.getPrice() == null) return templateResponse.error("Price is required");

            log.info("Product Save Success");
            return templateResponse.success(productRepository.save(request));
        } catch (Exception e) {
            log.error("Save Product Error: " + e.getMessage());
            return templateResponse.error("Save Product: " + e.getMessage());
        }
    }

    @Override
    public Map<Object, Object> update(Product request) {
        try {
            log.info("Updating product");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            UUID merchantId = (UUID) attribute.getRequest().getAttribute("merchantId");
            if (merchantId == null) return templateResponse.error("merchant id not found, please register as merchant");
            if (request.getId() == null) return templateResponse.error("Id is required");

            Product product = productRepository.getByIdAndMerchantId(request.getId(), merchantId);
            if (product == null) return templateResponse.error("Product not found");
            if (!request.getName().isEmpty()) product.setName(request.getName());
            if (request.getPrice() != null) product.setPrice(request.getPrice());

            log.info("Update Product Success");
            return templateResponse.success(productRepository.save(product));
        } catch (Exception e) {
            log.error("Update Product Error: " + e.getMessage());
            return templateResponse.error("Update Product: " + e.getMessage());
        }
    }

    @Override
    public Map<Object, Object> delete(Product request) {
        try {
            log.info("Delete Product");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            UUID merchantId = (UUID) attribute.getRequest().getAttribute("merchantId");
            if (merchantId == null) return templateResponse.error("merchant id not found, please register as merchant");
            if (request.getId() == null) return templateResponse.error("Id is required");

            Product product = productRepository.getByIdAndMerchantId(request.getId(), merchantId);
            if (product == null) return templateResponse.error("Product not found");

            log.info("Product Deleted");
            product.setDeletedDate(new Date());
            return templateResponse.success(productRepository.save(product));
        } catch (Exception e) {
            log.error("Delete Product Error: " + e.getMessage());
            return templateResponse.error("Delete Product : " + e.getMessage());
        }
    }

    @Override
    public Map<Object, Object> getById(UUID uuid) {
        try {
            log.info("Get Product");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            UUID merchantId = (UUID) attribute.getRequest().getAttribute("merchantId");
            if (merchantId == null) return templateResponse.error("merchant id not found, please register as merchant");
            if (uuid == null) return templateResponse.error("Id is required");

            Product product = productRepository.getByIdAndMerchantId(uuid, merchantId);
            if (product == null) return templateResponse.error("Product not found");

            log.info("Product Found");
            return templateResponse.success(product);
        } catch (Exception e) {
            log.error("Get Product Error: " + e.getMessage());
            return templateResponse.error("Get Product: " + e.getMessage());
        }
    }
}

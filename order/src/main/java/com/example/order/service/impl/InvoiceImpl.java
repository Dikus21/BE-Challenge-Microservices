package com.example.order.service.impl;

import com.example.order.dto.OrderDetailDTO;
import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import com.example.order.service.InvoiceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InvoiceImpl implements InvoiceService {
    public final String reportPath = "D:\\Andika\\BootCamp(SYNERGY)\\Project\\Challenge\\Challenge\\Challenge_7\\order\\report\\Invoice.jrxml";
    @Autowired
    public OrderRepository orderRepository;
    @Autowired
    private RestTemplateBuilder restTemplate;
    @Override
    public byte[] generateInvoice(Order request) throws IOException, JRException {
        log.info("startMakingInvoice");
        ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        Long userId = (Long) attribute.getRequest().getAttribute("userId");
        String username = (String) attribute.getRequest().getAttribute("username");
        String fullname = (String) attribute.getRequest().getAttribute("fullname");
        String merchantName = getMerchantName(userId);
        Order order = orderRepository.getById(request.getId());
        List<OrderDetailDTO> orderDetailDTOList = order.getOrderDetails().stream()
                .map(orderDetail -> {
                    OrderDetailDTO detailDTO = new OrderDetailDTO();
                    detailDTO.setDetailId(String.valueOf(orderDetail.getId()));
                    detailDTO.setProductName(orderDetail.getProduct().getName());
                    detailDTO.setQuantity(orderDetail.getQuantity());
                    detailDTO.setPrice(orderDetail.getProduct().getPrice());
                    detailDTO.setTotalPrice(orderDetail.getTotalPrice());
                    return detailDTO;
                })
                .collect(Collectors.toList());
        BigDecimal totalBill = orderDetailDTOList.stream()
                .map(OrderDetailDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        JRDataSource dataSource = new JRBeanCollectionDataSource(orderDetailDTOList);

        JasperReport jasperReport = JasperCompileManager.compileReport(reportPath);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("USERNAME", fullname);
        parameters.put("EMAIL", username);
        parameters.put("ORDER_ID", String.valueOf(order.getId()));
        parameters.put("DESTINATION_ADDRESS", order.getDestinationAddress());
        parameters.put("CREATED_DATE", new Date());
        parameters.put("MERCHANT_NAME", merchantName);
        parameters.put("TOTAL_BILL", totalBill);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        log.info("finished making invoice");

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public String getMerchantName(Long userId) throws JsonProcessingException {
        String url = "http://localhost:9093/api/v1/merchant/userId/" + userId;
        ResponseEntity<String> response = restTemplate.build().exchange(url, HttpMethod.GET, null, String.class);
        ObjectMapper mapper = new ObjectMapper();
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JsonNode root = mapper.readTree(response.getBody());
            String merchantName = root.get("data").get("name").asText();
            return merchantName;
        }
        return null;
    }
//    @Override
//    public byte[] generateInvoice(Users request) throws IOException, JRException {
//        log.info("startMakingInvoice");
//        Users users = userRepository.findByIdWithOrders(request.getId());
//        List<OrderDetailDTO> orderDetailDTOList = users.getOrders().stream()
//                .map(Order::getOrderDetails)
//                .flatMap(List::stream)
//                .map(orderDetail -> {
//                    OrderDetailDTO dto = new OrderDetailDTO();
//                    dto.setDetailId(String.valueOf(orderDetail.getId()));
//                    dto.setQuantity(orderDetail.getQuantity());
//                    dto.setTotalPrice(orderDetail.getTotalPrice());
//                    return dto;
//                })
//                .collect(Collectors.toList());
//
//        JRDataSource dataSource = new JRBeanCollectionDataSource(orderDetailDTOList);
//
//        JasperReport jasperReport = JasperCompileManager.compileReport(reportPath);
//
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("USER_ID", users.getId().toString());
//        parameters.put("USERNAME", users.getUsername());
//        parameters.put("USER_EMAIL", users.getEmailAddress());
//        parameters.put("CREATED_DATE_INVOICE", new Date());
//        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
//        log.info("finished making invoice");
//
//        return JasperExportManager.exportReportToPdf(jasperPrint);
//    }
}

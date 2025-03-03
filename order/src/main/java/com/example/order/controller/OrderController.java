package com.example.order.controller;

import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import com.example.order.service.OrderService;
import com.example.order.utils.SimpleStringUtils;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.util.*;

@RestController
@RequestMapping("/v1/order")
public class OrderController {
    @Autowired
    public OrderRepository orderRepository;
    @Autowired
    public OrderService orderService;

//    @Autowired
//    public InvoiceService invoiceService;

    @Autowired
    public SimpleStringUtils simpleStringUtils;

//    @PostMapping(value = {"/generateInvoice"})
//    public ResponseEntity<?> generateInvoice(@RequestBody Order request) {
//        try {
//            byte[] pdfData = invoiceService.generateInvoice(request);
//            ByteArrayInputStream bis = new ByteArrayInputStream(pdfData);
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-Disposition", "inline; filename=invoice.pdf");
//            return ResponseEntity
//                    .ok()
//                    .headers(headers)
//                    .contentType(MediaType.APPLICATION_PDF)
//                    .body(new InputStreamResource(bis));
//        } catch (Exception e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
//        }
//    }
    @PostMapping(value = {"/save", "/save/"})
    public ResponseEntity<Map> save(@RequestBody Order request, @RequestHeader("Authorization") String token) {
        return new ResponseEntity<Map>(orderService.save(request), HttpStatus.OK);
    }
    @GetMapping(value = {"/test", "/test/"})
    public ResponseEntity<Map> test() {
        Map test = new HashMap<>();
        test.put("test", "test");
        System.out.println("h");
        return new ResponseEntity<Map>(test, HttpStatus.OK);
    }
    @PutMapping(value = {"/update", "/update/"})
    public ResponseEntity<Map> update(@RequestBody Order request, @RequestHeader("Authorization") String token) {
        return new ResponseEntity<Map>(orderService.update(request), HttpStatus.OK);
    }

    @DeleteMapping(value = {"/delete", "/delete/"})
    public ResponseEntity<Map> delete(@RequestBody Order request, @RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(orderService.delete(request), HttpStatus.OK);
    }

    @GetMapping(value = {"/{id}", "/{id}/"})
    public ResponseEntity<Map> getById(@PathVariable("id") UUID id, @RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(orderService.getById(id), HttpStatus.OK);
    }

    @GetMapping(value = {"/showOrderDetail", "/showOrderDetail/"})
    public ResponseEntity<Map> getOrderDetailList(@RequestBody Order request, @RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(orderService.getOrderDetailList(request), HttpStatus.OK);
    }

    @GetMapping(value = {"/list", "/list/"})
    public ResponseEntity<Map> listOrder(
            @RequestParam() Integer page,
            @RequestParam(required = true) Integer size,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) UUID user,
            @RequestParam(required = false) boolean status,
            @RequestParam(required = false) String orderby,
            @RequestParam(required = false) String ordertype,
            @RequestHeader("Authorization") String token) {
        Pageable showData = simpleStringUtils.getShort(orderby, ordertype, page, size);

        Specification<Order> spec =
                ((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    if (address != null && !address.isEmpty()) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("destinationAddress")), "%" + address.toLowerCase() + "%"));
                    }
//                    if (username != null && !username.isEmpty()) {
//                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("user.username")), "%" + username.toLowerCase() + "%"));
//                    }
//                    if (user != null) {
//                        predicates.add(criteriaBuilder.equal(root.get("user.id"), user));
//                    }
                    predicates.add(criteriaBuilder.equal(root.get("completed"), status));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                });

        Page<Order> list = orderRepository.findAll(spec, showData);

        Map map = new HashMap();
        map.put("data",list);
        return new ResponseEntity<Map>(map, new HttpHeaders(), HttpStatus.OK);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Map> invalidFormatHandler(InvalidFormatException e) {
        Map<Object, Object> map = new HashMap<>();
        if (e.getTargetType().equals(UUID.class)) {
            map.put("ERROR", "Invalid UUID format provided in JSON");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
        map.put("ERROR", "Invalid data format");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }
}

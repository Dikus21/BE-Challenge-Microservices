package com.example.order.controller;

import com.example.order.entity.OrderDetail;
import com.example.order.repository.OrderDetailRepository;
import com.example.order.service.OrderDetailService;
import com.example.order.utils.SimpleStringUtils;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/v1/order_detail")
public class OrderDetailController {
    @Autowired
    public OrderDetailService orderDetailService;

    @Autowired
    public OrderDetailRepository orderDetailRepository;

    @Autowired
    public SimpleStringUtils simpleStringUtils;

    @PostMapping(value = {"/save", "/save/"})
    public ResponseEntity<Map> save(@RequestBody OrderDetail request) {
        return new ResponseEntity<Map>(orderDetailService.save(request), HttpStatus.OK);
    }

    @PutMapping(value = {"/update", "/update/"})
    public ResponseEntity<Map> update(@RequestBody OrderDetail request) {
        return new ResponseEntity<Map>(orderDetailService.update(request), HttpStatus.OK);
    }

    @DeleteMapping(value = {"/delete", "/delete/"})
    public ResponseEntity<Map> delete(@RequestBody OrderDetail request) {
        return new ResponseEntity<>(orderDetailService.delete(request), HttpStatus.OK);
    }

    @GetMapping(value = {"/{id}", "/{id}/"})
    public ResponseEntity<Map> getById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(orderDetailService.getById(id), HttpStatus.OK);
    }

    @GetMapping(value = {"/list", "/list/"})
    public ResponseEntity<Map> listQuizHeaderSpec(
            @RequestParam() Integer page,
            @RequestParam(required = true) Integer size,
            @RequestParam(required = false) BigDecimal quantity,
            @RequestParam(required = false) BigDecimal totalPrice,
            @RequestParam(required = false) String orderby,
            @RequestParam(required = false) String ordertype) {
        Pageable showData = simpleStringUtils.getShort(orderby, ordertype, page, size);

        Specification<OrderDetail> spec =
                ((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    if (quantity != null) {
                        predicates.add(criteriaBuilder.equal(root.get("quantity"), quantity));
                    }
                    if (totalPrice != null) {
                        predicates.add(criteriaBuilder.equal(root.get("totalPrice"), totalPrice));
                    }
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                });

        Page<OrderDetail> list = orderDetailRepository.findAll(spec, showData);

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

package com.example.merchant.controller;

import com.example.merchant.dto.PeriodReportDTO;
import com.example.merchant.dto.ReportDTO;
import com.example.merchant.entity.Merchant;
import com.example.merchant.repository.MerchantRepository;
import com.example.merchant.service.MerchantService;
import com.example.merchant.utils.SimpleStringUtils;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Predicate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/v1/merchant")
public class MerchantController {
    @Autowired
    public MerchantService merchantService;

    @Autowired
    public MerchantRepository merchantRepository;

    @Autowired
    public SimpleStringUtils simpleStringUtils;

    @PostMapping(value = {"/save", "/save/"})
    public ResponseEntity<Map> save(@RequestBody Merchant request) {
        return new ResponseEntity<Map>(merchantService.save(request), HttpStatus.OK);
    }

    @PutMapping(value = {"/update", "/update/"})
    public ResponseEntity<Map> update(@RequestBody Merchant request) {
        return new ResponseEntity<Map>(merchantService.update(request), HttpStatus.OK);
    }

    @DeleteMapping(value = {"/delete", "/delete/"})
    public ResponseEntity<Map> delete(@RequestBody Merchant request) {
        return new ResponseEntity<>(merchantService.delete(request), HttpStatus.OK);
    }

    @GetMapping(value = {"/{id}", "/{id}/"})
    public ResponseEntity<Map> getById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(merchantService.getById(id), HttpStatus.OK);
    }

    @GetMapping(value = {"/userId/{id}", "/userId/{id}/"})
    public ResponseEntity<Map> getByUserId(@PathVariable("id") Long id) {
        return new ResponseEntity<>(merchantService.getByUserId(id), HttpStatus.OK);
    }

    @GetMapping(value = {"/list", "/list/"})
    public ResponseEntity<Map> listQuizHeaderSpec(
            @RequestParam() Integer page,
            @RequestParam(required = true) Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) boolean open,
            @RequestParam(required = false) String orderby,
            @RequestParam(required = false) String ordertype) {
        Pageable showData = simpleStringUtils.getShort(orderby, ordertype, page, size);

        Specification<Merchant> spec =
                ((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    if (name != null && !name.isEmpty()) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
                    }
                    if (location != null && !location.isEmpty()) {
                        predicates.add(criteriaBuilder.equal(root.get("location"), location));
                    }
                    predicates.add(criteriaBuilder.equal(root.get("open"), open));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                });

        Page<Merchant> list = merchantRepository.findAll(spec, showData);

        Map map = new HashMap();
        map.put("data",list);
        return new ResponseEntity<Map>(map, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping(value = {"/income", "/income/"})
    public ResponseEntity<Map> report(
            @RequestParam() Integer page,
            @RequestParam(required = true) Integer size,
            @RequestParam(required = true) UUID uuid,
            @RequestParam(required = false)String sStartDate,
            @RequestParam(required = false, defaultValue = "Weekly")String period,
            @RequestParam(required = false) String orderby,
            @RequestParam(required = false) String ordertype) throws ParseException {
        Pageable showData = simpleStringUtils.getShort(orderby, ordertype, page, size);
        Merchant merchant = merchantRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Merchant not found"));
        Date startDate;
        if (sStartDate == null) startDate = merchant.getCreatedDate();
        else startDate = new SimpleDateFormat("yyyy-MM-dd").parse(sStartDate);
        Page<PeriodReportDTO> periodReportDTOList = merchantService.generateWeeklyReport(merchant, startDate, period, showData);
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setPeriod(period);
        reportDTO.setMerchantId(merchant.getId());
        reportDTO.setMerchantLocation(merchant.getLocation());
        reportDTO.setReports(periodReportDTOList);


        Map map = new HashMap();
        map.put("data",reportDTO);
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

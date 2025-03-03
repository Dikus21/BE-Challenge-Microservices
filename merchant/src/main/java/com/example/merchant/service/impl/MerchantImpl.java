package com.example.merchant.service.impl;

import com.example.merchant.dto.PeriodReportDTO;
import com.example.merchant.entity.Merchant;
import com.example.merchant.repository.MerchantRepository;
import com.example.merchant.service.MerchantService;
import com.example.merchant.utils.TemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class MerchantImpl implements MerchantService {

    @Autowired
    public MerchantRepository merchantRepository;
    @Autowired
    public TemplateResponse templateResponse;
    @Autowired
    private RestTemplateBuilder restTemplate;

    @Override
    public Map<Object, Object> save(Merchant request) {
        try {
            log.info("Save New Merchant");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            Long userId = (Long) attribute.getRequest().getAttribute("userId");
            if (merchantRepository.getByUserId(userId) != null) return templateResponse.error("You already have merchant account");
            request.setUserId(userId);

            if (request.getName().isEmpty()) return templateResponse.error("Name is required");
            if (merchantRepository.getByName(request.getName()) > 0) return templateResponse.error("Name Used");

            if (request.getLocation().isEmpty()) return templateResponse.error("Address is required");
            Merchant object = merchantRepository.save(request);
            if (!saveAuthorities(userId, object.getId())) return templateResponse.error("failed to save to user");
            log.info("Merchant Save Success");
            return templateResponse.success(object);
        } catch (Exception e) {
            log.error("Save Merchant Error: " + e.getMessage());
            return templateResponse.error("Merchant Merchant: " + e.getMessage());
        }
    }

    private boolean saveAuthorities(Long userId, UUID merchantId) {
        String url = "http://localhost:9091/api/v1/user-register/register-merchant";
        String merchantIdString = merchantId.toString();
        String requestBody =  "{\n" +
                "\"userId\":\"" + userId + "\",\n" +
                "\"merchantId\":\"" + merchantIdString + "\"\n" +
                "}";


        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "*/*");
        headers.set("Content-Type", "application/json");


        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.build().exchange(url, HttpMethod.PUT, entity, String.class);
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null;
    }

    @Override
    public Map<Object, Object> update(Merchant request) {
        try {
            log.info("Update Merchant");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            UUID merchantId = (UUID) attribute.getRequest().getAttribute("merchantId");
            Optional<Merchant> checkDataDBMerchant = merchantRepository.findById(merchantId);
            if (!checkDataDBMerchant.isPresent()) return templateResponse.error("Id is not Registered");
            if (!request.getName().isEmpty()) {
                if (merchantRepository.getByName(request.getName()) > 0) return templateResponse.error("Merchantname Exist, try other name");
                checkDataDBMerchant.get().setName(request.getName());
            }
            if (!request.getLocation().isEmpty()) {
                checkDataDBMerchant.get().setLocation(request.getLocation());
            }
            checkDataDBMerchant.get().setOpen(request.getOpen());

            log.info("Update Merchant Success");
            return templateResponse.success(merchantRepository.save(checkDataDBMerchant.get()));
        } catch (Exception e) {
            log.error("Update Merchant Error: " + e.getMessage());
            return templateResponse.error("Update Merchant: " + e.getMessage());
        }
    }

    @Override
    public Map<Object, Object> delete(Merchant request) {
        try {
            log.info("Delete Merchant");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            UUID merchantId = (UUID) attribute.getRequest().getAttribute("merchantId");
            Optional<Merchant> checkDataDBMerchant = merchantRepository.findById(merchantId);
            if (!checkDataDBMerchant.isPresent()) return templateResponse.error("Merchant not Found");

            log.info("Merchant Deleted");
            checkDataDBMerchant.get().setDeletedDate(new Date());
            return templateResponse.success(merchantRepository.save(checkDataDBMerchant.get()));
        } catch (Exception e) {
            log.error("Delete Merchant Error: " + e.getMessage());
            return templateResponse.error("Delete Merchant : " + e.getMessage());
        }
    }

    @Override
    public Map<Object, Object> getById(UUID uuid) {
        try {
            log.info("Get Merchant");
            ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            UUID merchantId = (UUID) attribute.getRequest().getAttribute("merchantId");

            Optional<Merchant> checkDataDBMerchant = merchantRepository.findById(merchantId);
            if (!checkDataDBMerchant.isPresent()) return templateResponse.error("Merchant not Found");

            log.info("Merchant Found");
            return templateResponse.success(checkDataDBMerchant.get());
        } catch (Exception e) {
            log.error("Get Merchant Error: " + e.getMessage());
            return templateResponse.error("Get Merchant: " + e.getMessage());
        }
    }

    @Override
    public Map<Object, Object> getByUserId(Long id) {
        try {
            log.info("Get Merchant by user Id");
            Merchant merchant = merchantRepository.getByUserId(id);
            if (merchant == null) return templateResponse.error("merchant not found");

            log.info("Merchant Found");
            return templateResponse.success(merchant);
        } catch (Exception e) {
            log.error("Get Merchant Error: " + e.getMessage());
            return templateResponse.error("Get Merchant: " + e.getMessage());
        }
    }

    @Override
    public Page<PeriodReportDTO> generateWeeklyReport(Merchant request, Date startDate, String period, Pageable pageable) {
        int dayInc = 7;
        if (period.equalsIgnoreCase("Monthly") || period.equalsIgnoreCase("Month")){
            dayInc = 30;
            period = "Month";
        } else {
            period = "Week";
        }
        log.info("Generate " + period + " Report");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, dayInc);
        Date endDate = calendar.getTime();
        Date currentDate = new Date();
        PeriodReportDTO periodReportDTO;
        List<PeriodReportDTO> periodReportDTOList = new ArrayList<>();
        int periodCount = 1;
        while (startDate.before(currentDate)) {
            log.info("start Date: " + startDate );
            log.info("end Date: " + endDate );
            periodReportDTO = new PeriodReportDTO();
            periodReportDTO.setStartDate(startDate);
            if (endDate.after(currentDate)) {
                long daysDiff = (currentDate.getTime() - startDate.getTime())/(24*60*60*1000);
                period = daysDiff + " days";
                calendar.add(Calendar.DAY_OF_MONTH, (int) (daysDiff-dayInc));
                endDate = calendar.getTime();
            }
            periodReportDTO.setEndDate(endDate);
            BigDecimal income = merchantRepository.sumIncomeMerchant(request.getId(), startDate, endDate);
            if (income == null) income = BigDecimal.valueOf(0);
            log.info("Income: " + income);
            periodReportDTO.setPeriod(period + " " + periodCount);
            periodCount++;
            periodReportDTO.setIncome(income);
            periodReportDTOList.add(periodReportDTO);
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            startDate = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, dayInc);
            endDate = calendar.getTime();
        }
//        return periodReportDTOList;
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), periodReportDTOList.size());
        List<PeriodReportDTO> subList = periodReportDTOList.subList(start, end);

        return new PageImpl<>(subList, pageable, periodReportDTOList.size());
    }

//    public WeeklyReportDTO generateMonthlyReport(UUID request) {
//        // Logic to generate monthly report
//        // ...
//    }
}

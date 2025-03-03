package com.example.merchant.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PeriodReportDTO {
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    private Date endDate;

    private String period;
    private BigDecimal income;
}

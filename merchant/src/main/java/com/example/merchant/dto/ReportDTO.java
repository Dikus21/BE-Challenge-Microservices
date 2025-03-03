package com.example.merchant.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.UUID;

@Data
public class ReportDTO {
    private UUID merchantId;
    private String merchantLocation;
    private String period;
    private Page<PeriodReportDTO> reports;
}

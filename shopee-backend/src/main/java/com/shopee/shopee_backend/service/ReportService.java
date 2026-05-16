package com.shopee.shopee_backend.service;

import com.shopee.shopee_backend.dto.SalesSummaryDto;

import java.io.IOException;
import java.time.LocalDate;

public interface ReportService {

    SalesSummaryDto getSalesSummary(Long franchiseId, LocalDate from, LocalDate to);

    byte[] exportPdf(Long franchiseId, LocalDate from, LocalDate to) throws IOException;

    byte[] exportExcel(Long franchiseId, LocalDate from, LocalDate to) throws IOException;
}

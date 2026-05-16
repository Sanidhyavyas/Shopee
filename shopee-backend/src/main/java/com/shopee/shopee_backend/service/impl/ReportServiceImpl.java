package com.shopee.shopee_backend.service.impl;

import com.shopee.shopee_backend.dto.SalesSummaryDto;
import com.shopee.shopee_backend.entity.Franchise;
import com.shopee.shopee_backend.entity.Order;
import com.shopee.shopee_backend.entity.OrderItem;
import com.shopee.shopee_backend.entity.OrderStatus;
import com.shopee.shopee_backend.exception.ResourceNotFoundException;
import com.shopee.shopee_backend.repository.FranchiseRepository;
import com.shopee.shopee_backend.repository.OrderRepository;
import com.shopee.shopee_backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;
    private final FranchiseRepository franchiseRepository;

    @Override
    @Transactional(readOnly = true)
    public SalesSummaryDto getSalesSummary(Long franchiseId, LocalDate from, LocalDate to) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise not found: " + franchiseId));

        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.atTime(LocalTime.MAX);

        List<Order> orders = orderRepository
                .findAllByFranchiseFranchiseIdAndCreatedAtBetweenOrderByCreatedAtDesc(franchiseId, fromDt, toDt);

        long total = orders.size();
        long completed = orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        long cancelled = orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();

        BigDecimal revenue = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgOrder = total > 0
                ? revenue.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Top products
        Map<Long, long[]> productSales = new LinkedHashMap<>();
        Map<Long, String> productNames = new HashMap<>();
        Map<Long, BigDecimal> productRevenue = new HashMap<>();

        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.CANCELLED) continue;
            for (OrderItem item : o.getItems()) {
                Long pid = item.getProduct().getProductId();
                productNames.put(pid, item.getProduct().getName());
                productSales.merge(pid, new long[]{item.getQuantity()},
                        (a, b) -> new long[]{a[0] + b[0]});
                productRevenue.merge(pid, item.getSubtotal(), BigDecimal::add);
            }
        }

        List<SalesSummaryDto.TopProductDto> topProducts = productSales.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue()[0], a.getValue()[0]))
                .limit(10)
                .map(e -> new SalesSummaryDto.TopProductDto(
                        productNames.get(e.getKey()),
                        null,
                        e.getValue()[0],
                        productRevenue.getOrDefault(e.getKey(), BigDecimal.ZERO)))
                .collect(Collectors.toList());

        // Daily revenue
        Map<LocalDate, BigDecimal> dailyRev = new TreeMap<>();
        Map<LocalDate, Long> dailyCount = new TreeMap<>();
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.CANCELLED) continue;
            LocalDate day = o.getCreatedAt().toLocalDate();
            dailyRev.merge(day, o.getTotalAmount(), BigDecimal::add);
            dailyCount.merge(day, 1L, Long::sum);
        }
        List<SalesSummaryDto.DailyRevenueDto> daily = dailyRev.entrySet().stream()
                .map(e -> new SalesSummaryDto.DailyRevenueDto(e.getKey(), e.getValue(),
                        dailyCount.getOrDefault(e.getKey(), 0L)))
                .collect(Collectors.toList());

        return new SalesSummaryDto(franchiseId, franchise.getOutletName(), from, to,
                total, completed, cancelled, revenue, avgOrder, topProducts, daily);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportPdf(Long franchiseId, LocalDate from, LocalDate to) throws IOException {
        SalesSummaryDto summary = getSalesSummary(franchiseId, from, to);

        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float margin = 50;
                float y = PDRectangle.A4.getHeight() - margin;
                float leading = 18;

                cs.beginText();
                cs.setFont(fontBold, 16);
                cs.newLineAtOffset(margin, y);
                cs.showText("Sales Report — " + summary.getFranchiseName());
                cs.endText();
                y -= leading * 2;

                cs.beginText();
                cs.setFont(fontNormal, 11);
                cs.newLineAtOffset(margin, y);
                cs.showText("Period: " + from + " to " + to);
                y -= leading;
                cs.newLineAtOffset(0, -leading);
                cs.showText("Total Orders: " + summary.getTotalOrders()
                        + "   Completed: " + summary.getCompletedOrders()
                        + "   Cancelled: " + summary.getCancelledOrders());
                y -= leading;
                cs.newLineAtOffset(0, -leading);
                cs.showText("Total Revenue: Rs. " + summary.getTotalRevenue()
                        + "   Avg Order Value: Rs. " + summary.getAverageOrderValue());
                cs.endText();
                y -= leading * 3;

                // Top products
                cs.beginText();
                cs.setFont(fontBold, 13);
                cs.newLineAtOffset(margin, y);
                cs.showText("Top Products");
                cs.endText();
                y -= leading;

                cs.beginText();
                cs.setFont(fontNormal, 10);
                cs.newLineAtOffset(margin, y);
                for (SalesSummaryDto.TopProductDto tp : summary.getTopProducts()) {
                    cs.showText(tp.getProductName() + " — Qty: " + tp.getQuantitySold()
                            + "  Revenue: Rs. " + tp.getRevenue());
                    cs.newLineAtOffset(0, -leading);
                    y -= leading;
                    if (y < margin + 40) break; // avoid overflow for large lists
                }
                cs.endText();
            }

            doc.save(out);
            return out.toByteArray();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportExcel(Long franchiseId, LocalDate from, LocalDate to) throws IOException {
        SalesSummaryDto summary = getSalesSummary(franchiseId, from, to);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Summary sheet
            Sheet summarySheet = workbook.createSheet("Summary");
            int r = 0;
            addRow(summarySheet, r++, "Franchise", summary.getFranchiseName());
            addRow(summarySheet, r++, "Period", from + " to " + to);
            addRow(summarySheet, r++, "Total Orders", String.valueOf(summary.getTotalOrders()));
            addRow(summarySheet, r++, "Completed Orders", String.valueOf(summary.getCompletedOrders()));
            addRow(summarySheet, r++, "Cancelled Orders", String.valueOf(summary.getCancelledOrders()));
            addRow(summarySheet, r++, "Total Revenue", "Rs. " + summary.getTotalRevenue());
            addRow(summarySheet, r++, "Avg Order Value", "Rs. " + summary.getAverageOrderValue());

            // Top products sheet
            Sheet productsSheet = workbook.createSheet("Top Products");
            Row header = productsSheet.createRow(0);
            header.createCell(0).setCellValue("Product");
            header.createCell(1).setCellValue("Qty Sold");
            header.createCell(2).setCellValue("Revenue (Rs.)");
            int pr = 1;
            for (SalesSummaryDto.TopProductDto tp : summary.getTopProducts()) {
                Row row = productsSheet.createRow(pr++);
                row.createCell(0).setCellValue(tp.getProductName());
                row.createCell(1).setCellValue(tp.getQuantitySold());
                row.createCell(2).setCellValue(tp.getRevenue().doubleValue());
            }

            // Daily revenue sheet
            Sheet dailySheet = workbook.createSheet("Daily Revenue");
            Row dailyHeader = dailySheet.createRow(0);
            dailyHeader.createCell(0).setCellValue("Date");
            dailyHeader.createCell(1).setCellValue("Revenue (Rs.)");
            dailyHeader.createCell(2).setCellValue("Order Count");
            int dr = 1;
            for (SalesSummaryDto.DailyRevenueDto d : summary.getDailyRevenue()) {
                Row row = dailySheet.createRow(dr++);
                row.createCell(0).setCellValue(d.getDate().toString());
                row.createCell(1).setCellValue(d.getRevenue().doubleValue());
                row.createCell(2).setCellValue(d.getOrderCount());
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void addRow(Sheet sheet, int rowNum, String key, String value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(key);
        row.createCell(1).setCellValue(value);
    }
}

package com.project.envantra.controller;

import com.project.envantra.model.request.DailyReportRequest;
import com.project.envantra.model.response.DailyReportResponse;
import com.project.envantra.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily")
    public DailyReportResponse getDailyReport(@ModelAttribute DailyReportRequest request) {
        return reportService.getDailyReport(request);
    }
}

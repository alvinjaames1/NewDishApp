package com.ratemydish.service;

import com.ratemydish.entity.Report;
import com.ratemydish.entity.ReportStatus;
import com.ratemydish.entity.User;
import com.ratemydish.exception.ResourceNotFoundException;
import com.ratemydish.exception.UnauthorizedException;
import com.ratemydish.repository.ReportRepository;
import com.ratemydish.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void updateReportStatus(Long reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        report.setStatus(status);
        reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Transactional
    public void reviewReport(Long reportId, ReportStatus status, UserDetails admin) {
        if (admin == null) {
            throw new UnauthorizedException("Authentication required");
        }

        User adminUser = userRepository.findByUsername(admin.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        report.setStatus(status);
        report.setReviewedByAdmin(adminUser);

        reportRepository.save(report);
    }
}
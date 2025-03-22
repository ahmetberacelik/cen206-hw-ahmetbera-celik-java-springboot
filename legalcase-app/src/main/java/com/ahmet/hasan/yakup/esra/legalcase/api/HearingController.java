package com.ahmet.hasan.yakup.esra.legalcase.api;

import com.ahmet.hasan.yakup.esra.legalcase.model.Hearing;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.HearingStatus;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IHearingService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/hearings")
public class HearingController {

    private static final Logger logger = LoggerFactory.getLogger(HearingController.class);

    private final IHearingService hearingService;

    @Autowired
    public HearingController(IHearingService hearingService) {
        this.hearingService = hearingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Hearing>> createHearing(@RequestBody Hearing hearing) {
        logger.info("REST request to create a new hearing");
        ApiResponse<Hearing> response = hearingService.createHearing(hearing);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.CREATED : HttpStatus.valueOf(response.getErrorCode()));
    }

    @PostMapping("/schedule")
    public ResponseEntity<ApiResponse<Hearing>> scheduleHearing(
            @RequestParam Long caseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hearingDate,
            @RequestParam String judge,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String notes) {

        logger.info("REST request to schedule a hearing for case ID: {}", caseId);
        ApiResponse<Hearing> response = hearingService.scheduleHearing(caseId, hearingDate, judge, location, notes);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.CREATED : HttpStatus.valueOf(response.getErrorCode()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Hearing>> getHearingById(@PathVariable Long id) {
        logger.info("REST request to get hearing by ID: {}", id);
        ApiResponse<Hearing> response = hearingService.getHearingById(id);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Hearing>>> getAllHearings() {
        logger.info("REST request to get all hearings");
        ApiResponse<List<Hearing>> response = hearingService.getAllHearings();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<ApiResponse<List<Hearing>>> getHearingsByCaseId(@PathVariable Long caseId) {
        logger.info("REST request to get hearings by case ID: {}", caseId);
        ApiResponse<List<Hearing>> response = hearingService.getHearingsByCaseId(caseId);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Hearing>>> getHearingsByStatus(@PathVariable HearingStatus status) {
        logger.info("REST request to get hearings by status: {}", status);
        ApiResponse<List<Hearing>> response = hearingService.getHearingsByStatus(status);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<Hearing>>> getHearingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        logger.info("REST request to get hearings between {} and {}", start, end);
        ApiResponse<List<Hearing>> response = hearingService.getHearingsByDateRange(start, end);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<Hearing>>> getUpcomingHearings() {
        logger.info("REST request to get upcoming hearings");
        ApiResponse<List<Hearing>> response = hearingService.getUpcomingHearings();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Hearing>> updateHearing(@PathVariable Long id, @RequestBody Hearing hearing) {
        logger.info("REST request to update hearing with ID: {}", id);
        if (hearing.getId() != null && !hearing.getId().equals(id)) {
            return new ResponseEntity<>(
                    ApiResponse.error("ID in the URL does not match the ID in the request body", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }
        ApiResponse<Hearing> response = hearingService.updateHearing(id, hearing);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Hearing>> updateHearingStatus(
            @PathVariable Long id,
            @RequestParam HearingStatus status) {

        logger.info("REST request to update status of hearing with ID: {} to {}", id, status);
        ApiResponse<Hearing> response = hearingService.updateHearingStatus(id, status);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<ApiResponse<Hearing>> rescheduleHearing(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDate) {

        logger.info("REST request to reschedule hearing with ID: {} to {}", id, newDate);
        ApiResponse<Hearing> response = hearingService.rescheduleHearing(id, newDate);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHearing(@PathVariable Long id) {
        logger.info("REST request to delete hearing with ID: {}", id);
        ApiResponse<Void> response = hearingService.deleteHearing(id);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.NO_CONTENT : HttpStatus.valueOf(response.getErrorCode()));
    }
}
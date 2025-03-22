package com.ahmet.hasan.yakup.esra.legalcase.service.virtual;

import com.ahmet.hasan.yakup.esra.legalcase.model.Hearing;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.HearingStatus;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface IHearingService {
    ApiResponse<Hearing> createHearing(Hearing hearing);
    ApiResponse<Hearing> scheduleHearing(Long caseId, LocalDateTime hearingDate, String judge, String location, String notes);
    ApiResponse<Hearing> getHearingById(Long id);
    ApiResponse<List<Hearing>> getAllHearings();
    ApiResponse<List<Hearing>> getHearingsByCaseId(Long caseId);
    ApiResponse<List<Hearing>> getHearingsByStatus(HearingStatus status);
    ApiResponse<List<Hearing>> getHearingsByDateRange(LocalDateTime start, LocalDateTime end);
    ApiResponse<List<Hearing>> getUpcomingHearings();
    ApiResponse<Hearing> updateHearing(Long id, Hearing hearing);
    ApiResponse<Hearing> updateHearingStatus(Long id, HearingStatus status);
    ApiResponse<Hearing> rescheduleHearing(Long id, LocalDateTime newDate);
    ApiResponse<Void> deleteHearing(Long id);
}
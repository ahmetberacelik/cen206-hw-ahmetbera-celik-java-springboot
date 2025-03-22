package com.ahmet.hasan.yakup.esra.legalcase.service.concrete;

import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Hearing;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.HearingStatus;
import com.ahmet.hasan.yakup.esra.legalcase.repository.CaseRepository;
import com.ahmet.hasan.yakup.esra.legalcase.repository.HearingRepository;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IHearingService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HearingService implements IHearingService {

    private static final Logger logger = LoggerFactory.getLogger(HearingService.class);

    private final HearingRepository hearingRepository;
    private final CaseRepository caseRepository;

    @Autowired
    public HearingService(HearingRepository hearingRepository, CaseRepository caseRepository) {
        this.hearingRepository = hearingRepository;
        this.caseRepository = caseRepository;
    }

    @Override
    public ApiResponse<Hearing> createHearing(Hearing hearing) {
        logger.info("Creating new hearing for case ID: {}", hearing.getCse() != null ? hearing.getCse().getId() : "unknown");

        try {
            // Validate case exists if provided
            if (hearing.getCse() != null && hearing.getCse().getId() != null) {
                Optional<Case> caseOptional = caseRepository.findById(hearing.getCse().getId());
                if (caseOptional.isEmpty()) {
                    return ApiResponse.error("Case not found with ID: " + hearing.getCse().getId(),
                            HttpStatus.NOT_FOUND.value());
                }
                hearing.setCse(caseOptional.get());
            }

            // Validate hearing date
            if (hearing.getHearingDate() == null) {
                return ApiResponse.error("Hearing date is required", HttpStatus.BAD_REQUEST.value());
            }

            Hearing savedHearing = hearingRepository.save(hearing);
            return ApiResponse.success(savedHearing);
        } catch (Exception e) {
            logger.error("Error creating hearing: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to create hearing: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ApiResponse<Hearing> scheduleHearing(Long caseId, LocalDateTime hearingDate, String judge, String location, String notes) {
        logger.info("Scheduling hearing for case ID: {} on {}", caseId, hearingDate);

        // Validate case exists
        Optional<Case> caseOptional = caseRepository.findById(caseId);
        if (caseOptional.isEmpty()) {
            return ApiResponse.error("Case not found with ID: " + caseId, HttpStatus.NOT_FOUND.value());
        }

        try {
            Hearing hearing = new Hearing();
            hearing.setCse(caseOptional.get());
            hearing.setHearingDate(hearingDate);
            hearing.setJudge(judge);
            hearing.setLocation(location);
            hearing.setNotes(notes);
            hearing.setStatus(HearingStatus.SCHEDULED);

            Hearing savedHearing = hearingRepository.save(hearing);
            return ApiResponse.success(savedHearing);
        } catch (Exception e) {
            logger.error("Error scheduling hearing: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to schedule hearing: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Hearing> getHearingById(Long id) {
        logger.info("Getting hearing by ID: {}", id);
        Optional<Hearing> hearingOptional = hearingRepository.findById(id);
        if (hearingOptional.isPresent()) {
            return ApiResponse.success(hearingOptional.get());
        } else {
            return ApiResponse.error("Hearing not found with ID: " + id, HttpStatus.NOT_FOUND.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Hearing>> getAllHearings() {
        logger.info("Getting all hearings");
        List<Hearing> hearings = hearingRepository.findAll();
        return ApiResponse.success(hearings);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Hearing>> getHearingsByCaseId(Long caseId) {
        logger.info("Getting hearings by case ID: {}", caseId);

        // Validate case exists
        if (!caseRepository.existsById(caseId)) {
            return ApiResponse.error("Case not found with ID: " + caseId, HttpStatus.NOT_FOUND.value());
        }

        List<Hearing> hearings = hearingRepository.findByCseId(caseId);
        return ApiResponse.success(hearings);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Hearing>> getHearingsByStatus(HearingStatus status) {
        logger.info("Getting hearings by status: {}", status);
        List<Hearing> hearings = hearingRepository.findByStatus(status);
        return ApiResponse.success(hearings);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Hearing>> getHearingsByDateRange(LocalDateTime start, LocalDateTime end) {
        logger.info("Getting hearings between {} and {}", start, end);

        if (start == null || end == null) {
            return ApiResponse.error("Start and end dates are required", HttpStatus.BAD_REQUEST.value());
        }

        if (start.isAfter(end)) {
            return ApiResponse.error("Start date must be before end date", HttpStatus.BAD_REQUEST.value());
        }

        List<Hearing> hearings = hearingRepository.findByHearingDateBetween(start, end);
        return ApiResponse.success(hearings);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Hearing>> getUpcomingHearings() {
        logger.info("Getting upcoming hearings");

        // Get hearings that are scheduled and have a future date
        List<Hearing> hearings = hearingRepository.findByHearingDateAfterAndStatusNot(
                LocalDateTime.now(), HearingStatus.CANCELLED);

        return ApiResponse.success(hearings);
    }

    @Override
    public ApiResponse<Hearing> updateHearing(Long id, Hearing hearing) {
        logger.info("Updating hearing with ID: {}", id);

        Optional<Hearing> existingHearing = hearingRepository.findById(id);
        if (existingHearing.isEmpty()) {
            return ApiResponse.error("Hearing not found with ID: " + id, HttpStatus.NOT_FOUND.value());
        }

        try {
            Hearing hearingToUpdate = existingHearing.get();

            // Update fields
            if (hearing.getHearingDate() != null) {
                hearingToUpdate.setHearingDate(hearing.getHearingDate());
            }

            if (hearing.getJudge() != null) {
                hearingToUpdate.setJudge(hearing.getJudge());
            }

            if (hearing.getLocation() != null) {
                hearingToUpdate.setLocation(hearing.getLocation());
            }

            if (hearing.getNotes() != null) {
                hearingToUpdate.setNotes(hearing.getNotes());
            }

            if (hearing.getStatus() != null) {
                hearingToUpdate.setStatus(hearing.getStatus());
            }

            // Only update case if provided and valid
            if (hearing.getCse() != null && hearing.getCse().getId() != null) {
                Optional<Case> caseOptional = caseRepository.findById(hearing.getCse().getId());
                if (caseOptional.isEmpty()) {
                    return ApiResponse.error("Case not found with ID: " + hearing.getCse().getId(),
                            HttpStatus.NOT_FOUND.value());
                }
                hearingToUpdate.setCse(caseOptional.get());
            }

            Hearing updatedHearing = hearingRepository.save(hearingToUpdate);
            return ApiResponse.success(updatedHearing);
        } catch (Exception e) {
            logger.error("Error updating hearing: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to update hearing: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ApiResponse<Hearing> updateHearingStatus(Long id, HearingStatus status) {
        logger.info("Updating status of hearing with ID: {} to {}", id, status);

        Optional<Hearing> existingHearing = hearingRepository.findById(id);
        if (existingHearing.isEmpty()) {
            return ApiResponse.error("Hearing not found with ID: " + id, HttpStatus.NOT_FOUND.value());
        }

        try {
            Hearing hearingToUpdate = existingHearing.get();
            hearingToUpdate.setStatus(status);

            Hearing updatedHearing = hearingRepository.save(hearingToUpdate);
            return ApiResponse.success(updatedHearing);
        } catch (Exception e) {
            logger.error("Error updating hearing status: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to update hearing status: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ApiResponse<Hearing> rescheduleHearing(Long id, LocalDateTime newDate) {
        logger.info("Rescheduling hearing with ID: {} to {}", id, newDate);

        if (newDate == null) {
            return ApiResponse.error("New hearing date is required", HttpStatus.BAD_REQUEST.value());
        }

        Optional<Hearing> existingHearing = hearingRepository.findById(id);
        if (existingHearing.isEmpty()) {
            return ApiResponse.error("Hearing not found with ID: " + id, HttpStatus.NOT_FOUND.value());
        }

        try {
            Hearing hearingToReschedule = existingHearing.get();

            // Keep track of the old date for logging
            LocalDateTime oldDate = hearingToReschedule.getHearingDate();

            // Update the hearing date and set status to SCHEDULED
            hearingToReschedule.setHearingDate(newDate);
            hearingToReschedule.setStatus(HearingStatus.SCHEDULED);

            // Add a note about rescheduling if notes field exists
            String existingNotes = hearingToReschedule.getNotes();
            String rescheduleNote = "Hearing rescheduled from " + oldDate + " to " + newDate;

            if (existingNotes != null && !existingNotes.isEmpty()) {
                hearingToReschedule.setNotes(existingNotes + "\n" + rescheduleNote);
            } else {
                hearingToReschedule.setNotes(rescheduleNote);
            }

            Hearing rescheduledHearing = hearingRepository.save(hearingToReschedule);
            return ApiResponse.success(rescheduledHearing);
        } catch (Exception e) {
            logger.error("Error rescheduling hearing: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to reschedule hearing: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ApiResponse<Void> deleteHearing(Long id) {
        logger.info("Deleting hearing with ID: {}", id);

        if (!hearingRepository.existsById(id)) {
            return ApiResponse.error("Hearing not found with ID: " + id, HttpStatus.NOT_FOUND.value());
        }

        try {
            hearingRepository.deleteById(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            logger.error("Error deleting hearing: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to delete hearing: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
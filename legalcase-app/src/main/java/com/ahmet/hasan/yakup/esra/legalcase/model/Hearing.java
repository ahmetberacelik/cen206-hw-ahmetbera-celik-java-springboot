package com.ahmet.hasan.yakup.esra.legalcase.model;

import com.ahmet.hasan.yakup.esra.legalcase.model.enums.HearingStatus;

import java.time.LocalDateTime;

/**
 * @brief Model class that holds hearing information.
 * @author Team
 * @date March 2025
 */
public class Hearing extends BaseEntity {
    private Case cse;
    private LocalDateTime hearingDate;
    private String judge;
    private HearingStatus status;

    /**
     * @brief Default constructor
     */
    public Hearing() {
        super();
        this.status = HearingStatus.SCHEDULED;
    }

    /**
     * @brief Parameterized constructor
     * @param id Hearing ID
     * @param cse Associated case
     * @param hearingDate Date and time of the hearing
     * @param judge Judge presiding over the hearing
     */
    public Hearing(Long id, Case cse, LocalDateTime hearingDate, String judge) {
        super(id);
        this.cse = cse;
        this.hearingDate = hearingDate;
        this.judge = judge;
        this.status = HearingStatus.SCHEDULED;
    }

    /**
     * @brief Get associated case
     * @return Associated case
     */
    public Case getCase() {
        return cse;
    }

    /**
     * @brief Set associated case
     * @param cse Case to set
     */
    public void setCase(Case cse) {
        this.cse = cse;
    }

    /**
     * @brief Get hearing date and time
     * @return Hearing date and time
     */
    public LocalDateTime getHearingDate() {
        return hearingDate;
    }

    /**
     * @brief Set hearing date and time
     * @param hearingDate Hearing date and time to set
     */
    public void setHearingDate(LocalDateTime hearingDate) {
        this.hearingDate = hearingDate;
    }

    /**
     * @brief Get judge name
     * @return Judge name
     */
    public String getJudge() {
        return judge;
    }

    /**
     * @brief Set judge name
     * @param judge Judge name to set
     */
    public void setJudge(String judge) {
        this.judge = judge;
    }

    /**
     * @brief Get hearing status
     * @return Hearing status
     */
    public HearingStatus getStatus() {
        return status;
    }

    /**
     * @brief Set hearing status
     * @param status Hearing status to set
     */
    public void setStatus(HearingStatus status) {
        this.status = status;
    }

    /**
     * @brief Reschedule the hearing to a new date
     * @param newDate New date and time for the hearing
     */
    public void reschedule(LocalDateTime newDate) {
        this.hearingDate = newDate;
        this.status = HearingStatus.SCHEDULED;
        this.preUpdate();
    }

    /**
     * @brief String representation of the hearing
     * @return String representing the hearing object
     */
    @Override
    public String toString() {
        return "Hearing{" +
                "id=" + getId() +
                ", case=" + (cse != null ? cse.getCaseNumber() : "none") +
                ", hearingDate=" + hearingDate +
                ", judge='" + judge + '\'' +
                ", status=" + status +
                '}';
    }
}
-- Create hearings table
CREATE TABLE IF NOT EXISTS hearings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    scheduled_date DATETIME NOT NULL,
    actual_start_time DATETIME,
    actual_end_time DATETIME,
    location VARCHAR(255) NOT NULL,
    judge_name VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_case_id (case_id),
    INDEX idx_scheduled_date (scheduled_date),
    INDEX idx_status (status)
);

-- Create sample data
INSERT INTO hearings (case_id, title, description, scheduled_date, location, status)
VALUES 
(1, 'Initial Hearing', 'First hearing for the case', DATE_ADD(NOW(), INTERVAL 7 DAY), 'Courtroom 101', 'SCHEDULED'),
(1, 'Evidence Review', 'Review evidence for the case', DATE_ADD(NOW(), INTERVAL 14 DAY), 'Courtroom 102', 'SCHEDULED'),
(2, 'Witness Examination', 'Examination of witnesses', DATE_ADD(NOW(), INTERVAL 5 DAY), 'Courtroom 203', 'SCHEDULED'),
(3, 'Final Hearing', 'Final decision on the case', DATE_ADD(NOW(), INTERVAL 30 DAY), 'Main Courtroom', 'SCHEDULED'); 
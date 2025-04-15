-- Initial schema for Case Service

CREATE TABLE IF NOT EXISTS cases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_number VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,
    open_date DATE NOT NULL,
    close_date DATE,
    client_id BIGINT NOT NULL,
    assigned_user_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_case_number (case_number),
    INDEX idx_client_id (client_id),
    INDEX idx_assigned_user_id (assigned_user_id),
    INDEX idx_status (status)
); 
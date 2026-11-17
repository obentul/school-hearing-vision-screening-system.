-- School Hearing and Vision Screening Assistant
-- Run this file in MySQL Workbench or the MySQL command line.
CREATE DATABASE IF NOT EXISTS school_screening
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE school_screening;

CREATE TABLE IF NOT EXISTS app_users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(60) NOT NULL UNIQUE,
    password_hash VARCHAR(300) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    role ENUM('ADMIN','TEACHER','SCREENING_OFFICER') NOT NULL DEFAULT 'TEACHER',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pupils (
    pupil_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admission_no VARCHAR(40) NOT NULL UNIQUE,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    class_name VARCHAR(40) NOT NULL,
    sex ENUM('F','M','OTHER','NOT_RECORDED') NOT NULL DEFAULT 'NOT_RECORDED',
    date_of_birth DATE NULL,
    guardian_contact VARCHAR(40) NULL,
    notes VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS screenings (
    screening_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pupil_id BIGINT NOT NULL,
    screening_date DATE NOT NULL,
    screening_type ENUM('HEARING','VISION') NOT NULL,
    outcome ENUM('PASS','REFER','INCONCLUSIVE') NOT NULL,
    method_notes VARCHAR(500) NULL,
    recorded_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_screening_pupil FOREIGN KEY (pupil_id) REFERENCES pupils(pupil_id),
    CONSTRAINT fk_screening_user FOREIGN KEY (recorded_by) REFERENCES app_users(user_id),
    INDEX idx_screenings_date (screening_date),
    INDEX idx_screenings_pupil (pupil_id)
);

CREATE TABLE IF NOT EXISTS referrals (
    referral_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pupil_id BIGINT NOT NULL,
    screening_id BIGINT NULL,
    referral_date DATE NOT NULL,
    reason VARCHAR(500) NOT NULL,
    destination VARCHAR(160) NULL,
    status ENUM('PENDING','REFERRED','FOLLOW_UP_DONE','CLOSED') NOT NULL DEFAULT 'PENDING',
    follow_up_notes VARCHAR(500) NULL,
    recorded_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_referral_pupil FOREIGN KEY (pupil_id) REFERENCES pupils(pupil_id),
    CONSTRAINT fk_referral_screening FOREIGN KEY (screening_id) REFERENCES screenings(screening_id) ON DELETE SET NULL,
    CONSTRAINT fk_referral_user FOREIGN KEY (recorded_by) REFERENCES app_users(user_id),
    INDEX idx_referrals_status (status)
);



DROP TABLE IF EXISTS jobs;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS projects;

CREATE TABLE users (
    user_id VARCHAR(100) NOT NULL PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL
);

CREATE TABLE projects (
    project_id VARCHAR(100) NOT NULL PRIMARY KEY,
    project_name VARCHAR(100) NOT NULL
);

CREATE TABLE jobs (
    job_id VARCHAR(100) NOT NULL PRIMARY KEY,
    status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED') NOT NULL,
    value_result INT NULL,
    error TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    user_id VARCHAR(100) NOT NULL,
    project_id VARCHAR(100) NULL,

    CONSTRAINT jobs_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT jobs_project FOREIGN KEY (project_id) REFERENCES projects(project_id)
);

INSERT INTO users (user_id, user_name) VALUES
('user123', 'John Doe'),
('user456', 'Jane Doe'),
('user789', 'Jake Smith');

INSERT INTO projects (project_id, project_name) VALUES
('p-1234', 'Project 1234'),
('p-5678', 'Project 5678'),
('p-9999', 'Project 9999');

CREATE SEQUENCE IF NOT EXISTS employee_seq;

CREATE TABLE IF NOT EXISTS employee (
    employee_id BIGINT NOT NULL DEFAULT nextval('employee_seq') PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS project_seq;

CREATE TABLE IF NOT EXISTS project (
    project_id BIGINT NOT NULL DEFAULT nextval('project_seq') PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    stage VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    start_date DATE,
    end_date DATE
);

CREATE TABLE IF NOT EXISTS project_employee (
     project_id BIGINT REFERENCES project,
    employee_id BIGINT REFERENCES employee
);

CREATE SEQUENCE IF NOT EXISTS user_accounts_seq;

CREATE TABLE IF NOT EXISTS user_accounts (
    user_id BIGINT NOT NULL DEFAULT nextval('user_accounts_seq') PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN DEFAULT true
);

CREATE SEQUENCE IF NOT EXISTS permission_seq;

CREATE TABLE IF NOT EXISTS permissions (
    permission_id BIGINT NOT NULL DEFAULT nextval('permission_seq') PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_permissions (
    user_id BIGINT REFERENCES user_accounts(user_id) ON DELETE CASCADE,
    permission_id BIGINT REFERENCES permissions(permission_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, permission_id)
);
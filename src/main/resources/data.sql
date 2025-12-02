-- INSERT EMPLOYEES
INSERT INTO employee (employee_id, first_name, last_name, email) VALUES (nextval('employee_seq'), 'John', 'Warton', 'warton@gmail.com');
INSERT INTO employee (employee_id, first_name, last_name, email) VALUES (nextval('employee_seq'), 'Mike', 'Lanister', 'lanister@gmail.com');
INSERT INTO employee (employee_id, first_name, last_name, email) VALUES (nextval('employee_seq'), 'Steve', 'Reeves', 'reeves@gmail.com');
INSERT INTO employee (employee_id, first_name, last_name, email) VALUES (nextval('employee_seq'), 'Ronald', 'Connor', 'connor@gmail.com');
INSERT INTO employee (employee_id, first_name, last_name, email) VALUES (nextval('employee_seq'), 'Jim', 'Salvator', 'salvator@gmail.com');
INSERT INTO employee (employee_id, first_name, last_name, email) VALUES (nextval('employee_seq'), 'Peter', 'Henley', 'henley@gmail.com');

-- INSERT PROJECTS
INSERT INTO project (project_id, name, stage, description, start_date, end_date) VALUES (nextval('project_seq'), 'Large Production Deploy', 'NOTSTARTED', 'Final deployment of software into production', '2025-02-01', '2025-04-30');
INSERT INTO project (project_id, name, stage, description, start_date, end_date) VALUES (nextval('project_seq'), 'New Employee Budget', 'COMPLETED', 'New employee bonus budget planning', '2025-01-01', '2025-01-31');
INSERT INTO project (project_id, name, stage, description, start_date, end_date) VALUES (nextval('project_seq'), 'Office Reconstruction', 'INPROGRESS', 'Reconstruction of Monroe office building', '2025-01-15', '2025-06-30');
INSERT INTO project (project_id, name, stage, description, start_date, end_date) VALUES (nextval('project_seq'), 'Improve Intranet Security', 'INPROGRESS', 'Security improvements after data breach', '2025-02-01', '2025-05-31');

-- INSERT PROJECT_EMPLOYEE RELATIONS
INSERT INTO project_employee (project_id, employee_id) SELECT p.project_id, e.employee_id FROM project p, employee e WHERE p.name = 'Large Production Deploy' AND e.last_name = 'Warton';
INSERT INTO project_employee (project_id, employee_id) SELECT p.project_id, e.employee_id FROM project p, employee e WHERE p.name = 'Large Production Deploy' AND e.last_name = 'Reeves';
INSERT INTO project_employee (project_id, employee_id) SELECT p.project_id, e.employee_id FROM project p, employee e WHERE p.name = 'Large Production Deploy' AND e.last_name = 'Connor';
INSERT INTO project_employee (project_id, employee_id) SELECT p.project_id, e.employee_id FROM project p, employee e WHERE p.name = 'New Employee Budget' AND e.last_name = 'Lanister';
INSERT INTO project_employee (project_id, employee_id) SELECT p.project_id, e.employee_id FROM project p, employee e WHERE p.name = 'New Employee Budget' AND e.last_name = 'Warton';
INSERT INTO project_employee (project_id, employee_id) SELECT p.project_id, e.employee_id FROM project p, employee e WHERE p.name = 'Office Reconstruction' AND e.last_name = 'Henley';
INSERT INTO project_employee (project_id, employee_id) SELECT p.project_id, e.employee_id FROM project p, employee e WHERE p.name = 'Office Reconstruction' AND e.last_name = 'Salvator';
INSERT INTO project_employee (project_id, employee_id) SELECT p.project_id, e.employee_id FROM project p, employee e WHERE p.name = 'Improve Intranet Security' AND e.last_name = 'Reeves';
INSERT INTO project_employee (project_id, employee_id) SELECT p.project_id, e.employee_id FROM project p, employee e WHERE p.name = 'Improve Intranet Security' AND e.last_name = 'Henley';

-- INSERT USER ACCOUNTS
-- Admin user (username: admin, email: admin@admin.adm, password: admin)
INSERT INTO user_accounts (user_id, username, email, password, role, enabled, is_admin, is_reviewer) 
VALUES (nextval('user_accounts_seq'), 'admin', 'admin@admin.adm',
        '$2a$12$7Ucy9BgqTH8NoNrkvDr.dOTUfTph4Jn6Jw3FyNhjgA6pcfvJ5HjiK', 'ROLE_ADMIN', true, true, false);

-- Reviewer user (username: reviewer, email: reviewer@example.com, password: reviewer)
INSERT INTO user_accounts (user_id, username, email, password, role, enabled, is_admin, is_reviewer) 
VALUES (nextval('user_accounts_seq'), 'reviewer', 'reviewer@example.com',
        '$2a$10$s/hR5qMC8.qPG5brReJed.doBh9c1j2KU8tZP9QZ1VyCevRfjSYf6', 'ROLE_USER', true, false, true);

-- Regular user (username: user, email: user@example.com, password: user)
INSERT INTO user_accounts (user_id, username, email, password, role, enabled, is_admin, is_reviewer) 
VALUES (nextval('user_accounts_seq'), 'user', 'user@example.com',
        '$2a$10$9Pdb7i4fdb1YHUByM987nOL2uqFnLV6HIMoXFhab.TB7HKStnc/c2', 'ROLE_USER', true, false, false);
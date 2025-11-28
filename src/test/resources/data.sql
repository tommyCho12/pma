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

-- INSERT USER ACCOUNT (username: admin, password: admin)
INSERT INTO user_accounts (user_id, username, email, password, role, enabled) VALUES (nextval('user_accounts_seq'), 'admin', 'admin@admin.adm',
                                                                                      '$2b$12$7Ucy9BgqTH8NoNrkvDr.dOTUfTph4Jn6Jw3FyNhjgA6pcfvJ5HjiK', 'ROLE_ADMIN', true);
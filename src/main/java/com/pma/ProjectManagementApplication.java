package com.pma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectManagementApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ProjectManagementApplication.class, args);
    }
    //INSERT INTO user_accounts (user_id, email, enabled, password, role, username) VALUES (0, 'admin@admin.adm', true, '$2b$12$7Ucy9BgqTH8NoNrkvDr.dOTUfTph4Jn6Jw3FyNhjgA6pcfvJ5HjiK', 'ROLE_ADMIN', 'admin');
    //username:admin
    //password:admin
}

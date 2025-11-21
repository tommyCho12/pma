# Project Management Application (PMA)

## Overview
This project is a web-based application designed to manage projects, employees, and their assignments. It provides features such as tracking project timelines, employee participation, and generating charts for data visualization.

## Technologies Used
- **Java**: Backend development
- **Spring Boot**: Framework for building the application
- **SQL**: Database management
- **JavaScript**: Frontend development
- **Maven**: Dependency management and build tool

## Features
- **Project Management**: Create, update, and delete projects.
- **Employee Management**: Manage employee details and their assignments to projects.
- **Data Visualization**: Generate charts for project timelines, employee participation, and other metrics.
- **Database Operations**: Includes SQL scripts for managing database tables.

## Project Structure
- `src/main/java/com/pma/dto`: Contains DTO interfaces for data transfer objects such as `ChartData`, `EmployeeProjects`, and `TimelinesChartData`.
- `src/test/resources/drop.sql`: SQL script for dropping tables if they exist.
- `src/main/resources`: Configuration files and other resources.

## Prerequisites
- **Java 17 or higher**
- **Maven 3.8 or higher**
- **Spring Boot 3.x**
- **Database**: Compatible with SQL-based databases (e.g., MySQL, PostgreSQL).

## Setup Instructions
1. Clone the repository:
      git clone https://github.com/<your-username>/<repository-name>.git

2. Navigate to the project directory:<pre>cd <repository-name> </pre>
3. Build the project using Maven:<pre>mvn clean install </pre>
4. Run the application:<pre>mvn spring-boot:run </pre>
5. Access the application at http://localhost:8080.

## Database Setup
1. Create a database in your SQL server.
2. Run the SQL script located at src/test/resources/drop.sql to initialize the database tables.

## Contributing
Contributions are welcome! Please fork the repository and submit a pull request.  
Contact
For any questions or issues, please contact [tomasnt4@gmail.com].

package com.pma.dao;

import com.pma.dto.EmployeeProjects;
import com.pma.entities.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "employeesapi", path = "employeesapi")
public interface IEmployeeRepository extends PagingAndSortingRepository<Employee, Long>
{
    @Override
    List<Employee> findAll();

    @Query(nativeQuery = true,
            value =
            "SELECT e.first_name AS firstName, e.last_name as lastName, COUNT (pe.employee_id) as projectCount " +
            "FROM EMPLOYEE e LEFT JOIN PROJECT_EMPLOYEE pe ON e.employee_id=pe.employee_id " +
            "GROUP BY e.first_name, e.last_name ORDER BY 3 DESC")
    List<EmployeeProjects> getProjectsCount();

    Employee findByEmail(String value);

    Employee findByEmployeeId(long id);

}

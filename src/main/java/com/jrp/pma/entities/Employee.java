package com.jrp.pma.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jrp.pma.validators.UniqueValue;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Entity
public class Employee
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_generator")
    @SequenceGenerator(name = "employee_generator", sequenceName = "employee_seq", allocationSize = 1)
    private long employeeId;

    @NotBlank(message = "*Must have first name!")
    @Size(min=2, max=50)
    private String firstName;

    @NotBlank(message = "*Must have last name!")
    @Size(min=2, max=50)
    private String lastName;

    @NotBlank(message = "*Must have valid email!")
    @Email
    @UniqueValue
    private String email;

    @SuppressWarnings("JpaDataSourceORMInspection")
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
    fetch = FetchType.LAZY)
    @JoinTable(name = "project_employee",
            joinColumns = @JoinColumn(name="employee_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"))
    @JsonIgnore //ignores field for serialization to prevent infinite loop.
    private List<Project> projects;

    public List<Project> getProjects()
    {
        return projects;
    }

    public void setProjects(List<Project> projects)
    {
        this.projects = projects;
    }

    public Employee()
    {
    }

    public Employee(String firstName, String lastName, String email)
    {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public long getEmployeeId()
    {
        return employeeId;
    }

    public void setEmployeeId(long employeeId)
    {
        this.employeeId = employeeId;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}

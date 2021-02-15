package com.jrp.pma.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
public class Project
{
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "project_generator")
    @SequenceGenerator(name = "project_generator", sequenceName = "project_seq", allocationSize = 1)
    private long projectId;

    @NotBlank(message = "*Name is mandatory!")
    @Size(min = 2, max = 50)
    private String name;

    private String stage;

    @NotNull
    @Size(max = 100)
    private String description;

    @NotNull(message = "Cannot be empty!")
    private Date startDate;

    @NotNull(message = "Cannot be empty!")
    private Date endDate;

    @SuppressWarnings("JpaDataSourceORMInspection")
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    @JoinTable(name = "project_employee",
            joinColumns = @JoinColumn(name="project_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"))
    @JsonIgnore //ignores field for serialization to prevent infinite loop.
    private List<Employee> employees;

    public List<Employee> getEmployees()
    {
        return employees;
    }

    public void setEmployees(List<Employee> employees)
    {
        this.employees = employees;
    }

    public Project()
    {
    }

    public Project( String name, String stage, String description, Date startDate, Date endDate)
    {
        this.name = name;
        this.stage = stage;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getProjectId()
    {
        return projectId;
    }

    public void setProjectId(long projectId)
    {
        this.projectId = projectId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getStage()
    {
        return stage;
    }

    public void setStage(String stage)
    {
        this.stage = stage;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }
}

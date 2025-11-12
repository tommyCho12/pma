package com.pma.services;

import com.pma.dao.IEmployeeRepository;
import com.pma.entities.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService
{
    @Autowired
    IEmployeeRepository empRepo;
    @Autowired
    private RestTemplateAutoConfiguration restTemplateAutoConfiguration;

    public Iterable<Employee> findAll(){
        return empRepo.findAll();
    }

    public Employee findByEmployeeId(long id){ return empRepo.findByEmployeeId(id); }

    public void save(Employee e){
        empRepo.save(e);
    }

    public void delete(Employee e){ empRepo.delete(e); }
}

package com.pma.controllers;

import com.pma.entities.Employee;
import com.pma.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/employees")
public class EmployeeController
{
    @Autowired
    EmployeeService empService;

    @GetMapping
    public String display(Model model){
        Iterable<Employee> employees = empService.findAll();
        model.addAttribute("employeesList", employees);
        return "employees/employees-home";
    }

    @GetMapping("/new")
    public String displayEmployeeForm(Model model){
        Employee anEmployee = new Employee();
        model.addAttribute("employee", anEmployee);
        return "employees/new-employee";
    }

    @GetMapping("/update")
    public String displayEmployeeFormUpdate(Model model, @RequestParam("id") Long id){
        Employee anEmployee = empService.findByEmployeeId(id);
        model.addAttribute("employee", anEmployee);
        return "employees/new-employee";
    }

    @GetMapping("/delete")
    public String deleteEmployee(Model model, @RequestParam("id") Long id){
        Employee emp = empService.findByEmployeeId(id);
        empService.delete(emp);
        return "redirect:/employees";
    }

    @PostMapping("/save")
    public String createEmployee(Model model, @Valid Employee emp, Errors errors){
        if(errors.hasErrors())
            return "employees/new-employee";

        empService.save(emp);
        return "redirect:/employees"; //redirect to prevent multiple saves.
    }


}

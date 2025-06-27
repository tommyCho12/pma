package com.pma.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pma.dto.TimelinesChartData;
import com.pma.entities.Employee;
import com.pma.entities.Project;
import com.pma.services.EmployeeService;
import com.pma.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController
{
    @Autowired
    ProjectService proService;

    @Autowired
    EmployeeService empService;

    @GetMapping
    public String display(Model model){
        List<Project> projects = proService.findAll();
        model.addAttribute("projectsList", projects);
        return "projects/projects-home";
    }

    @GetMapping("/new")
    public String displayProjectForm(Model model){
        Project aProject = new Project();
        Iterable<Employee> employees = empService.findAll();
        model.addAttribute("project", aProject);
        model.addAttribute("allEmployees", employees);
        return "projects/new-project";
    }

    @PostMapping("/save")
    public String createProject(Model model, @Valid Project project, BindingResult br){
        if(br.hasErrors())
            return "projects/new-project";

        proService.save(project);
        return "redirect:/projects"; //redirect to prevent multiple saves.
    }

    @GetMapping("/update")
    public String displayProjectFormUpdate(Model model, @RequestParam("id") Long id){
        Project p = proService.findByProjectId(id);
        model.addAttribute("project", p);
        return "projects/new-project";
    }

    @GetMapping("/delete")
    public String deleteProject(Model model, @RequestParam("id") Long id){
        Project p = proService.findByProjectId(id);
        proService.delete(p);
        return "redirect:/projects";
    }

    @GetMapping("/timelines")
    public String displayProjectTimelines(Model model) throws JsonProcessingException
    {
        List<TimelinesChartData> data = proService.getProjectTimelines();
        String jsonString = new ObjectMapper().writeValueAsString(data);
        model.addAttribute("timelinesData", jsonString);
        return "projects/project-timelines";
    }

}

package com.jrp.pma.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jrp.pma.dao.IEmployeeRepository;
import com.jrp.pma.dao.IProjectRepository;
import com.jrp.pma.dto.EmployeeProjects;
import com.jrp.pma.dto.ChartData;
import com.jrp.pma.entities.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController
{
    @Value("${version}")
    String version;

    @Autowired
    IProjectRepository proRepo;

    @Autowired
    IEmployeeRepository empRepo;

    @GetMapping
    public String display(Model model) throws JsonProcessingException
    {
        model.addAttribute("version", version);
        List<Project> projects = proRepo.findAll();
        List<EmployeeProjects> employeeProjectsCount = empRepo.getProjectsCount();
        model.addAttribute("projectsList", projects);
        model.addAttribute("employeeProjectCount", employeeProjectsCount);

        List<ChartData> chartData = proRepo.getProjectStageCount();         //convert chartData object to JSON to be able to be processed by js.
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(chartData);     //"[["NOTSTARTED", 1], ["INPROGRESS", 2], ["COMPLETED", 1]]"
        model.addAttribute("projectStatusCnt", jsonString);

        return "main/home";
    }
}

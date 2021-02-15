package com.jrp.pma.services;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.jrp.pma.dao.IProjectRepository;
import com.jrp.pma.dto.TimelinesChartData;
import com.jrp.pma.entities.Employee;
import com.jrp.pma.entities.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.PropertyResourceBundle;

@Service
public class ProjectService
{
    @Autowired
    IProjectRepository proRepo;

    public List<Project> findAll(){
        return proRepo.findAll();
    }

    public Project findByProjectId(Long id) { return proRepo.findByProjectId(id); }

    public List<TimelinesChartData> getProjectTimelines() { return proRepo.getProjectTimelines(); }

    public void save(Project p){
        proRepo.save(p);
    }

    public void delete(Project p){ proRepo.delete(p); }

}

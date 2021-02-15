package com.jrp.pma.api.controllers;

import com.jrp.pma.dao.IProjectRepository;
import com.jrp.pma.entities.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/app-api/projects")
public class ProjectApiController
{
    @Autowired
    IProjectRepository proRepo;

    @GetMapping
    public List<Project> getProjects(){
        return proRepo.findAll();
    }

    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable("id") Long id){
        return proRepo.findById(id).orElse(null);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Project create(@RequestBody @Valid Project project){
        return proRepo.save(project);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Project update(@RequestBody @Valid Project Project){

        return proRepo.save(Project);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public Project partialUpdate(@PathVariable("id") Long id, @RequestBody @Valid Project patchProject){
        Project pro = proRepo.findById(id).orElse(null);
        if(patchProject.getDescription() != null)
            pro.setDescription(patchProject.getDescription());
        if(patchProject.getName() != null)
            pro.setName(patchProject.getName());
        if(patchProject.getStage() != null)
            pro.setStage(patchProject.getStage());

        return proRepo.save(pro);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id){
        try
        {
            proRepo.deleteById(id);
        }
        catch(EmptyResultDataAccessException e)
        {
            //
        }
    }
}

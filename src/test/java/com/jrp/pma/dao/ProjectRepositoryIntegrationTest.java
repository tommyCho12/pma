package com.jrp.pma.dao;

import com.jrp.pma.entities.Project;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProjectRepositoryIntegrationTest
{
    @Autowired
    IProjectRepository proRepo;

    @Test
    public void ifProjectSaved_thenSuccess(){
        int initialSize = proRepo.findAll().size();
        Project newProject = new Project(
                "NEW_project",
                "NOT_STARTED",
                "test description",
                Date.valueOf(LocalDate.now()),
                Date.valueOf(LocalDate.now().plusDays(2))
        );
        proRepo.save(newProject);
        assertEquals(initialSize + 1, proRepo.findAll().size());
    }

}

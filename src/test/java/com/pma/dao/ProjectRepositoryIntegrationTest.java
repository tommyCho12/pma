package com.pma.dao;

import com.pma.entities.Project;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
                scripts = {"classpath:schema.sql", "classpath:data.sql"}),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
                scripts = "classpath:drop.sql")
})
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
        assertTrue(proRepo.findAll().stream().anyMatch(proj -> proj.getName().equals("NEW_project")));
    }

}

package com.pma.dao;

import com.pma.dto.ChartData;
import com.pma.dto.TimelinesChartData;
import com.pma.entities.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "projectsapi", path = "projectsapi")
public interface IProjectRepository extends PagingAndSortingRepository<Project, Long>
{
    @Override
    List<Project> findAll();

    @Query(nativeQuery = true, value=
            "SELECT stage as label, COUNT(*) as value " +
            "FROM project " +
            "GROUP BY stage")
    List<ChartData> getProjectStageCount();

    @Query(nativeQuery = true,
    value = "SELECT name as name, start_date as startDate, end_date as endDate " +
            "FROM project " +
            "WHERE start_date is not null ")
    List<TimelinesChartData> getProjectTimelines();

    Project findByProjectId(long id);
}

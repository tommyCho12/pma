package com.pma.dto;

import java.util.Date;

public interface TimelinesChartData
{
    //Need to match the alias in the query
    String getName();
    Date getStartDate();
    Date getEndDate();
}

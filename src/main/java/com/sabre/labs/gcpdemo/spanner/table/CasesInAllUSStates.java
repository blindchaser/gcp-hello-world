package com.sabre.labs.gcpdemo.spanner.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

@Table(name = "cases_in_all_us_states")
@Data
public class CasesInAllUSStates {

    @PrimaryKey
    @JsonProperty("USAState")
    @Column(name = "usa_state")
    private String USAState;

    @JsonProperty("TotalCases")
    @Column(name = "total_cases")
    private String TotalCases;

    @JsonProperty("NewCases")
    @Column(name = "new_cases")
    private String NewCases;

    @JsonProperty("TotalDeaths")
    @Column(name = "total_deaths")
    private String TotalDeaths;

    @JsonProperty("NewDeaths")
    @Column(name = "new_deaths")
    private String NewDeaths;

    @JsonProperty("ActiveCases")
    @Column(name = "active_cases")
    private String ActiveCases;

    @JsonProperty("TotalTests")
    @Column(name = "total_tests")
    private String TotalTests;

    @JsonProperty("Tot_Cases_1M_Pop")
    @Column(name = "tot_cases_1m_pop")
    private String Tot_Cases_1M_Pop;

    @JsonProperty("Deaths_1M_Pop")
    @Column(name = "deaths_1m_pop")
    private String Deaths_1M_Pop;

    @JsonProperty("Tests_1M_Pop")
    @Column(name = "tests_1m_pop")
    private String Tests_1M_Pop;
}

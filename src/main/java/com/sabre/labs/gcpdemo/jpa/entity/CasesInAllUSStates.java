package com.sabre.labs.gcpdemo.jpa.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class CasesInAllUSStates {

    @Id
    @JsonProperty("USAState")
    private String USAState;

    @JsonProperty("TotalCases")
    private String TotalCases;

    @JsonProperty("NewCases")
    private String NewCases;

    @JsonProperty("TotalDeaths")
    private String TotalDeaths;

    @JsonProperty("NewDeaths")
    private String NewDeaths;

    @JsonProperty("ActiveCases")
    private String ActiveCases;

    @JsonProperty("TotalTests")
    private String TotalTests;

    @JsonProperty("Tot_Cases_1M_Pop")
    private String Tot_Cases_1M_Pop;

    @JsonProperty("Deaths_1M_Pop")
    private String Deaths_1M_Pop;

    @JsonProperty("Tests_1M_Pop")
    private String Tests_1M_Pop;
}

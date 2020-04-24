package com.sabre.labs.gcpdemo.controller;

import com.sabre.labs.gcpdemo.feign.Covid19FeignClient;
import com.sabre.labs.gcpdemo.jpa.entity.CasesInAllUSStates;
import com.sabre.labs.gcpdemo.jpa.repository.CasesInAllUSStateRepositoryJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class COVID19Controller {
    private final CasesInAllUSStateRepositoryJPA repository;
    private final Covid19FeignClient client;

    @GetMapping(value = "", produces = "application/json")
    public List<CasesInAllUSStates> getAll() {
        return (List<CasesInAllUSStates>) repository.findAll();
    }

    @GetMapping(value = "/update", produces = "application/json")
    public List<CasesInAllUSStates> updateAndGetAll() {
        List<CasesInAllUSStates> casesInAllUSStates = client.getAll().get(0).getData().get(0).getTable();
        return (List<CasesInAllUSStates>) repository.saveAll(casesInAllUSStates);
    }

    @GetMapping(value = "/find/{state}", produces = "application/json")
    public List<CasesInAllUSStates> findByStateName(@PathVariable String state) {
        return repository.findCasesInAllUSStatesByUSAStateIgnoreCaseContaining(state);
    }
}

package com.sabre.labs.gcpdemo.jpa.repository;

import com.sabre.labs.gcpdemo.jpa.entity.CasesInAllUSStates;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CasesInAllUSStateRepositoryJPA extends CrudRepository<CasesInAllUSStates, String> {
    List<CasesInAllUSStates> findCasesInAllUSStatesByUSAStateIgnoreCaseContaining(String state);
}

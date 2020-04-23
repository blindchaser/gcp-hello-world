package com.sabre.labs.gcpdemo.spanner.repository;

import com.sabre.labs.gcpdemo.spanner.table.CasesInAllUSStates;
import org.springframework.cloud.gcp.data.spanner.repository.SpannerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CasesInAllUSStateRepository extends SpannerRepository<CasesInAllUSStates, String> {
    List<CasesInAllUSStates> findCasesInAllUSStatesByUSAStateIgnoreCaseContaining(String state);
}

package com.sabre.labs.gcpdemo.feign.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CasesInAllUSStateTable {
    private List<CasesInAllUSStates> table;
}

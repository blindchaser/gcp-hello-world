package com.sabre.labs.gcpdemo.feign.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CasesInAllUSStatesData {
    private List<CasesInAllUSStateTable> data;
}

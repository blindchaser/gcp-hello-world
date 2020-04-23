package com.sabre.labs.gcpdemo.feign;

import com.sabre.labs.gcpdemo.feign.dtos.CasesInAllUSStatesData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(url = "https://covid19-server.chrismichael.now.sh/api/v1/CasesInAllUSStates",
        name = "covid-19",
        configuration = FeignClientConfig.class)
public interface Covid19FeignClient {
    @GetMapping("/")
    List<CasesInAllUSStatesData> getAll();
}

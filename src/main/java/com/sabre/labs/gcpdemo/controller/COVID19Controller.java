package com.sabre.labs.gcpdemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.sabre.labs.gcpdemo.feign.Covid19FeignClient;
import com.sabre.labs.gcpdemo.feign.dtos.CasesInAllUSStates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RestController
@RequiredArgsConstructor
public class COVID19Controller {
    private final Covid19FeignClient client;

    private final ObjectMapper mapper;

    private final Storage storage;

    private final static String BUCKET_NAME = "qwiklabs-gcp-03-7197bd37f0a4";

    private void createFile(byte[] file, String fileName) {
        storage.create(BlobInfo.newBuilder(BUCKET_NAME, fileName + ".json").build(),
                file);
    }

    @GetMapping(value = "", produces = "application/json")
    public List<String> getAll() {
        Bucket bucket = storage.get(BUCKET_NAME);
        Page<Blob> blobs = bucket.list();
        return StreamSupport.stream(blobs.getValues().spliterator(), false)
                .map(blob -> {
                    log.info("blob name: {}", blob.getName());
                    return blob.getName();
                })
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/update", produces = "application/json")
    public List<CasesInAllUSStates> updateAndGetAll() {
        List<CasesInAllUSStates> casesInAllUSStates = client.getAll().get(0).getData().get(0).getTable();
        casesInAllUSStates.forEach(cases -> {
            try {
                createFile(mapper.writeValueAsBytes(cases), cases.getUSAState());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        return casesInAllUSStates;
    }
//
//    @GetMapping(value = "/find/{state}", produces = "application/json")
//    public List<CasesInAllUSStates> findByStateName(@PathVariable String state) {
//        return repository.findCasesInAllUSStatesByUSAStateIgnoreCaseContaining(state);
//    }
}

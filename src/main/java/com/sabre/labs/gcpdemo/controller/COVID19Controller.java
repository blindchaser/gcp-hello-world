package com.sabre.labs.gcpdemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.sabre.labs.gcpdemo.feign.Covid19FeignClient;
import com.sabre.labs.gcpdemo.feign.dtos.CasesInAllUSStates;
import com.sabre.labs.gcpdemo.spanner.StorageMetaRepository;
import com.sabre.labs.gcpdemo.spanner.table.StorageMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class COVID19Controller {
    private final Covid19FeignClient client;

    private final ObjectMapper mapper;

    private final static String BUCKET_NAME = "qwiklabs-gcp-04-ea5cdb2f0588";

    private final StorageMetaRepository repository;
    private final Storage storage;
    private final Bucket bucket;

    public COVID19Controller(Covid19FeignClient client, ObjectMapper mapper, StorageMetaRepository repository, Storage storage) {
        this.client = client;
        this.mapper = mapper;
        this.repository = repository;
        this.storage = storage;
        this.bucket = storage.get(BUCKET_NAME);
    }

    private Blob createFile(byte[] file, String fileName) {
        return bucket.create(fileName + ".json", file);
//        storage.create(BlobInfo.newBuilder(BUCKET_NAME, fileName + ".json").build(),
//                file);
    }

    @GetMapping(value = "", produces = "application/json")
    public List<CasesInAllUSStates> getAll() {
        return ((List<StorageMeta>) repository.findAll()).stream()
                .map(meta -> {
                    try {
                        return mapper.readValue(bucket.get(meta.getName()).getContent(), CasesInAllUSStates.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/update", produces = "application/json")
    public List<CasesInAllUSStates> updateAndGetAll() {
        List<CasesInAllUSStates> casesInAllUSStates = client.getAll().get(0).getData().get(0).getTable();
        casesInAllUSStates.forEach(cases -> {
            try {
                Blob blob = createFile(mapper.writeValueAsBytes(cases), cases.getUSAState());
                repository.save(StorageMeta.builder()
                        .usaState(cases.getUSAState())
                        .name(blob.getBlobId().getName())
                        .build());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        return casesInAllUSStates;
    }

    @GetMapping(value = "/find/{state}", produces = "application/json")
    public List<CasesInAllUSStates> findByStateName(@PathVariable String state) {
        return repository.findStorageMetasByUsaStateIgnoreCaseContaining(state).stream()
                .map(meta -> {
                    try {
                        return mapper.readValue(bucket.get(meta.getName()).getContent(), CasesInAllUSStates.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }
}

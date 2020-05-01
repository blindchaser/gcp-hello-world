package com.sabre.labs.gcpdemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Storage;
import com.sabre.labs.gcpdemo.ConfigProperty;
import com.sabre.labs.gcpdemo.feign.Covid19FeignClient;
import com.sabre.labs.gcpdemo.feign.dtos.CasesInAllUSStates;
import com.sabre.labs.gcpdemo.spanner.StorageMetaRepository;
import com.sabre.labs.gcpdemo.spanner.table.StorageMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gcp.storage.GoogleStorageResource;
import org.springframework.core.io.WritableResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class COVID19Controller {
    private final Covid19FeignClient client;
    private final ConfigProperty configProperty;
    private final ObjectMapper mapper;
    private final Storage storage;
    private final StorageMetaRepository repository;

    public COVID19Controller(Covid19FeignClient client, ConfigProperty configProperty, ObjectMapper mapper, Storage storage, StorageMetaRepository repository) {
        this.client = client;
        this.configProperty = configProperty;
        this.mapper = mapper;
        this.storage = storage;
        this.repository = repository;
    }

    private String createFile(byte[] file, String fileName) {
        try {
            WritableResource resource = new GoogleStorageResource(storage, "gs://" + configProperty.getBucketName() + "/" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            OutputStream os = resource.getOutputStream();
            os.write(file);
            os.close();
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getFile(String fileName) {
        WritableResource resource;
        try {
            resource = new GoogleStorageResource(storage, "gs://" + configProperty.getBucketName() + "/" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            return resource.getInputStream().readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping(value = "", produces = "application/json")
    public List<CasesInAllUSStates> getAll() {
        return ((List<StorageMeta>) repository.findAll()).stream()
                .map(meta -> {
                    try {
                        return mapper.readValue(getFile(meta.getName()), CasesInAllUSStates.class);
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
                String fileName = createFile(mapper.writeValueAsBytes(cases), cases.getUSAState());
                repository.save(StorageMeta.builder()
                        .usaState(cases.getUSAState())
                        .name(fileName)
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
                        return mapper.readValue(getFile(meta.getName()), CasesInAllUSStates.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }
}

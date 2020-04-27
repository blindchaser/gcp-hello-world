package com.sabre.labs.gcpdemo.spanner;

import com.sabre.labs.gcpdemo.spanner.table.StorageMeta;
import org.springframework.cloud.gcp.data.spanner.repository.SpannerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageMetaRepository extends SpannerRepository<StorageMeta, String> {
    List<StorageMeta> findStorageMetasByUsaStateIgnoreCaseContaining(String state);
}

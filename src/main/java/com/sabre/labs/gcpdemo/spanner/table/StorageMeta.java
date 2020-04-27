package com.sabre.labs.gcpdemo.spanner.table;

import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

@Data
@Builder
@Table(name = "storage_meta")
public class StorageMeta {
    @PrimaryKey
    private String usaState;
    private String name;
}

package com.sabre.labs.gcpdemo.spanner;

import com.sabre.labs.gcpdemo.spanner.table.StorageMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gcp.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import org.springframework.cloud.gcp.data.spanner.core.admin.SpannerSchemaUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class SpannerSchemaTool {
    private final SpannerDatabaseAdminTemplate databaseAdmin;
    private final SpannerSchemaUtils schemaUtils;

    public void setUp() {
        if (!this.databaseAdmin.tableExists("storage_meta")) {
            String createString = this.schemaUtils.getCreateTableDdlString(StorageMeta.class);
            this.databaseAdmin.executeDdlStrings(Collections.singletonList(createString), true);
        }
    }
}

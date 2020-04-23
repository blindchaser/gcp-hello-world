package com.sabre.labs.gcpdemo.spanner;

import com.sabre.labs.gcpdemo.spanner.table.CasesInAllUSStates;
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
        if (!this.databaseAdmin.tableExists("cases_in_all_us_states")) {
            String createStrings = this.schemaUtils.getCreateTableDdlString(CasesInAllUSStates.class);
            this.databaseAdmin.executeDdlStrings(Collections.singletonList(createStrings), true);
        }
    }
}

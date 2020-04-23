package com.sabre.labs.gcpdemo.spanner;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gcp.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import org.springframework.cloud.gcp.data.spanner.core.admin.SpannerSchemaUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SapnnerSchemaTool {

    private final SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;

    private final SpannerSchemaUtils spannerSchemaUtils;

}

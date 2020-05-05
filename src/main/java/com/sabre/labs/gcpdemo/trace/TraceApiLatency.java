package com.sabre.labs.gcpdemo.trace;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import io.opencensus.common.Scope;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;
import java.io.IOException;
import java.util.Date;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class TraceApiLatency {
    // [START trace_setup_java_custom_span]
    private static final Tracer tracer = Tracing.getTracer();

    public static void traceApi() {
        // Create a child Span of the current Span.
        try (Scope ss = tracer.spanBuilder("MyChildWorkSpan").startScopedSpan()) {
            traceInitialWork();
            tracer.getCurrentSpan().addAnnotation("Finished initial work");
            traceFinalWork();
        }
    }

    private static void traceInitialWork() {
        // ...
        tracer.getCurrentSpan().addAnnotation("Doing initial work");
        // ...
    }

    private static void traceFinalWork() {
        // ...
        tracer.getCurrentSpan().addAnnotation("Hello world!");
        // ...
    }
    // [END trace_setup_java_custom_span]

    // [START trace_setup_java_full_sampling]
    public static void doWorkFullSampled() {
        try (Scope ss =
                     tracer
                             .spanBuilder("MyChildWorkSpan")
                             .setSampler(Samplers.alwaysSample())
                             .startScopedSpan()) {
            traceInitialWork();
            tracer.getCurrentSpan().addAnnotation("Finished initial work");
            traceFinalWork();
        }
    }
    // [END trace_setup_java_full_sampling]

}

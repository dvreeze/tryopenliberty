/*
 * Copyright 2024-2024 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.tryopenliberty.quoteswebapp.healthcheck;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.tryopenliberty.quoteswebapp.model.Quote;
import eu.cdevreeze.tryopenliberty.quoteswebapp.service.QuoteService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;
import jakarta.json.spi.JsonProvider;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

import java.io.StringWriter;
import java.lang.management.*;
import java.util.Arrays;

/**
 * Multiple health checks.
 *
 * @author Chris de Vreeze
 */
@ApplicationScoped
public class HealthChecks {

    private HealthChecks() {
    }

    @Produces
    @Liveness
    public HealthCheck heapMemoryHealthCheck() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();

        return () -> HealthCheckResponse.named("heapMemoryUsage")
                .status(heapMemoryUsage.getUsed() < heapMemoryUsage.getMax() * 0.9)
                .withData("maxHeapMemory", String.valueOf(heapMemoryUsage.getMax()))
                .withData("usedHeapMemory", String.valueOf(heapMemoryUsage.getUsed()))
                .withData("heapMemory", heapMemoryUsage.toString())
                .build();
    }

    @Produces
    @Liveness
    public HealthCheck nonHeapMemoryHealthCheck() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();

        boolean status = nonHeapMemoryUsage.getMax() < 0 || nonHeapMemoryUsage.getUsed() < nonHeapMemoryUsage.getMax() * 0.9;

        return () -> HealthCheckResponse.named("nonHeapMemoryUsage")
                .status(status)
                .withData("maxNonHeapMemory", String.valueOf(nonHeapMemoryUsage.getMax()))
                .withData("committedNonHeapMemory", String.valueOf(nonHeapMemoryUsage.getCommitted()))
                .withData("usedNonHeapMemory", String.valueOf(nonHeapMemoryUsage.getUsed()))
                .withData("nonHeapMemory", nonHeapMemoryUsage.toString())
                .build();
    }

    @Produces
    @Liveness
    public HealthCheck threadHealthCheck() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        boolean status = threadMXBean.findDeadlockedThreads() == null || threadMXBean.findDeadlockedThreads().length > 0;

        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);

        JsonArray threadInfosAsJson = convertToJson(threadInfos);
        StringWriter sw = new StringWriter();
        JsonWriter jsonWriter = jsonProvider.createWriter(sw);
        jsonWriter.writeArray(threadInfosAsJson);
        String threadInfosAsJsonString = sw.toString();

        return () ->
                HealthCheckResponse.named("threading")
                        .status(status)
                        .withData("threadCount", threadMXBean.getThreadCount())
                        .withData("threadData", threadInfosAsJsonString)
                        .build();
    }

    @Produces
    @Readiness
    public HealthCheck invokeQuoteServiceHealthCheck(QuoteService quoteService) {
        try {
            ImmutableList<Quote> quotes = quoteService.findAllQuotes();
            Preconditions.checkArgument(!quotes.isEmpty());

            return () -> HealthCheckResponse.named("invokeQuoteService")
                    .status(true)
                    .withData("numberOfQuotesFound", String.valueOf(quotes.size()))
                    .build();
        } catch (RuntimeException e) {
            return () -> HealthCheckResponse.named("invokeQuoteService")
                    .status(false)
                    .withData("exception", e.toString())
                    .build();
        }
    }

    private JsonArray convertToJson(ThreadInfo[] threadInfos) {
        return jsonProvider.createArrayBuilder(
                Arrays.stream(threadInfos).map(this::convertToJson).toList()
        ).build();
    }

    private JsonObject convertToJson(ThreadInfo threadInfo) {
        return jsonProvider.createObjectBuilder()
                .add("threadId", threadInfo.getThreadId())
                .add("threadName", threadInfo.getThreadName())
                .add("threadState", threadInfo.getThreadState().toString())
                .add("stackTrace", convertToJson(threadInfo.getStackTrace()))
                .build();
    }

    private JsonArray convertToJson(StackTraceElement[] stackTraceElements) {
        return jsonProvider.createArrayBuilder(
                Arrays.stream(stackTraceElements).map(e -> jsonProvider.createValue(e.toString())).toList()
        ).build();
    }

    private static JsonProvider jsonProvider = JsonProvider.provider();
}

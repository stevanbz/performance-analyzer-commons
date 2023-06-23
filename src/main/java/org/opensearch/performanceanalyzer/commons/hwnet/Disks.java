/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.hwnet;

import java.util.*;
import org.opensearch.performanceanalyzer.commons.collectors.DiskMetrics;
import org.opensearch.performanceanalyzer.commons.hwnet.metrics.DiskMetricsCalculator;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.impl.DiskObserver;
import org.opensearch.performanceanalyzer.commons.metrics_generator.DiskMetricsGenerator;
import org.opensearch.performanceanalyzer.commons.metrics_generator.linux.LinuxDiskMetricsGenerator;
import org.opensearch.performanceanalyzer.commons.observer.ResourceObserver;

public class Disks {
    private static Map<String, Map<String, Object>> diskKVMap = new HashMap<>();
    private static Map<String, Map<String, Object>> olddiskKVMap = new HashMap<>();
    private static long kvTimestamp = 0;
    private static long oldkvTimestamp = 0;
    private static final ResourceObserver diskObserver = new DiskObserver();
    private static LinuxDiskMetricsGenerator linuxDiskMetricsHandler = new LinuxDiskMetricsGenerator();

    static {
        oldkvTimestamp = System.currentTimeMillis();
        kvTimestamp = oldkvTimestamp;
    }

    private static StringBuilder value = new StringBuilder();
    public static DiskMetricsGenerator getDiskMetricsHandler() {
        return linuxDiskMetricsHandler;
    }

    public static void addSample() {
        olddiskKVMap.clear();
        olddiskKVMap.putAll(diskKVMap);
        diskKVMap.clear();

        diskKVMap.putAll(diskObserver.observe());

        oldkvTimestamp = kvTimestamp;
        kvTimestamp = System.currentTimeMillis();

        calculateDiskMetrics();
    }

    private static void calculateDiskMetrics() {

        linuxDiskMetricsHandler.setDiskMetricsMap(
                DiskMetricsCalculator.calculateDiskMetrics(
                        kvTimestamp,
                        oldkvTimestamp,
                        diskKVMap,
                        olddiskKVMap
                )
        );
    }

    public static Map<String, DiskMetrics> getMetricsMap() {
       return DiskMetricsCalculator.calculateDiskMetrics(kvTimestamp, oldkvTimestamp, diskKVMap, olddiskKVMap);
    }

    public static void runOnce() {
        addSample();
        System.out.println("disks: " +  DiskMetricsCalculator.calculateDiskMetrics(
                kvTimestamp,
                oldkvTimestamp,
                diskKVMap,
                olddiskKVMap
        ));
    }
}

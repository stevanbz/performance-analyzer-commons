/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.hwnet.metrics;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.opensearch.performanceanalyzer.commons.collectors.DiskMetrics;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.impl.DiskObserver;

public class DiskMetricsCalculatorTests {

    @Test
    public void testCalculateDiskMetrics() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;
        long start = 9000;
        long end = 10000;

        Map<String, Map<String, Object>> endTimeResourceMetrics = new HashMap<>();
        Map<String, Object> endTimeMetricMap =
                Map.of(
                        DiskObserver.DiskKeys.NAME.getLabel(), "disk-1",
                        DiskObserver.DiskKeys.RTIME.getLabel(), endTimeInMillis,
                        DiskObserver.DiskKeys.WTIME.getLabel(), endTimeInMillis,
                        DiskObserver.DiskKeys.RDONE.getLabel(), end,
                        DiskObserver.DiskKeys.WDONE.getLabel(), end,
                        DiskObserver.DiskKeys.RSECTORS.getLabel(), end,
                        DiskObserver.DiskKeys.WSECTORS.getLabel(), end);
        endTimeResourceMetrics.put("disk-1", endTimeMetricMap);

        Map<String, Map<String, Object>> startTimeResourceMetrics = new HashMap<>();
        Map<String, Object> startTimeMetricMap =
                Map.of(
                        DiskObserver.DiskKeys.NAME.getLabel(), "disk-1",
                        DiskObserver.DiskKeys.RTIME.getLabel(), startTimeInMillis,
                        DiskObserver.DiskKeys.WTIME.getLabel(), startTimeInMillis,
                        DiskObserver.DiskKeys.RDONE.getLabel(), start,
                        DiskObserver.DiskKeys.WDONE.getLabel(), start,
                        DiskObserver.DiskKeys.RSECTORS.getLabel(), start,
                        DiskObserver.DiskKeys.WSECTORS.getLabel(), start);
        startTimeResourceMetrics.put("disk-1", startTimeMetricMap);
        Map<String, DiskMetrics> result =
                DiskMetricsCalculator.calculateDiskMetrics(
                        endTimeInMillis,
                        startTimeInMillis,
                        endTimeResourceMetrics,
                        startTimeResourceMetrics);
        assertNotNull(result);
        assertTrue(result.containsKey("disk-1"));

        DiskMetrics diskMetric = result.get("disk-1");

        assertEquals(2, diskMetric.utilization, 0d);
        assertEquals(10, diskMetric.await, 0d);
        assertEquals(0.0512, diskMetric.serviceRate, 0d);
    }

    @Test
    public void testCalculateDiskMetricsStartTimeSameAsEndTime() {
        long startTimeInMillis = 1553725339;
        Map<String, DiskMetrics> result =
                DiskMetricsCalculator.calculateDiskMetrics(
                        startTimeInMillis,
                        startTimeInMillis,
                        Collections.emptyMap(),
                        Collections.emptyMap());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCalculateIOMetricsMissingKeys() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;

        Map<String, Map<String, Object>> endTimeResourceMetrics = new HashMap<>();
        Map<String, Map<String, Object>> startTimeResourceMetrics = new HashMap<>();

        Map<String, DiskMetrics> result =
                DiskMetricsCalculator.calculateDiskMetrics(
                        startTimeInMillis,
                        endTimeInMillis,
                        endTimeResourceMetrics,
                        startTimeResourceMetrics);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        Map<String, Object> endTimeMetricMap = new HashMap<>();
        endTimeMetricMap.put(DiskObserver.DiskKeys.NAME.getLabel(), "disk-1");
        endTimeMetricMap.put(DiskObserver.DiskKeys.RTIME.getLabel(), endTimeInMillis);
        endTimeMetricMap.put(DiskObserver.DiskKeys.WTIME.getLabel(), endTimeInMillis);
        endTimeResourceMetrics.put("disk-1", endTimeMetricMap);

        Map<String, Object> startTimeMetricMap = new HashMap<>();
        startTimeMetricMap.put(DiskObserver.DiskKeys.NAME.getLabel(), "disk-1");
        startTimeMetricMap.put(DiskObserver.DiskKeys.RTIME.getLabel(), startTimeInMillis);
        startTimeMetricMap.put(DiskObserver.DiskKeys.WTIME.getLabel(), startTimeInMillis);
        startTimeResourceMetrics.put("disk-1", startTimeMetricMap);

        result =
                DiskMetricsCalculator.calculateDiskMetrics(
                        endTimeInMillis,
                        startTimeInMillis,
                        endTimeResourceMetrics,
                        startTimeResourceMetrics);

        DiskMetrics diskMetric = result.get("disk-1");

        assertNotNull(diskMetric);
        assertEquals(2, diskMetric.utilization, 0d);
        assertEquals(0, diskMetric.await, 0d);
        assertEquals(0, diskMetric.serviceRate, 0d);

        long start = 9000;
        long end = 10000;

        endTimeMetricMap.put(DiskObserver.DiskKeys.RDONE.getLabel(), end);
        endTimeMetricMap.put(DiskObserver.DiskKeys.WDONE.getLabel(), end);

        startTimeMetricMap.put(DiskObserver.DiskKeys.RDONE.getLabel(), start);
        startTimeMetricMap.put(DiskObserver.DiskKeys.WDONE.getLabel(), start);

        result =
                DiskMetricsCalculator.calculateDiskMetrics(
                        endTimeInMillis,
                        startTimeInMillis,
                        endTimeResourceMetrics,
                        startTimeResourceMetrics);
        diskMetric = result.get("disk-1");

        assertEquals(2, diskMetric.utilization, 0d);
        assertEquals(10, diskMetric.await, 0d);

        endTimeMetricMap.put(DiskObserver.DiskKeys.RSECTORS.getLabel(), end);
        endTimeMetricMap.put(DiskObserver.DiskKeys.WSECTORS.getLabel(), end);

        startTimeMetricMap.put(DiskObserver.DiskKeys.RSECTORS.getLabel(), start);
        startTimeMetricMap.put(DiskObserver.DiskKeys.WSECTORS.getLabel(), start);

        result =
                DiskMetricsCalculator.calculateDiskMetrics(
                        endTimeInMillis,
                        startTimeInMillis,
                        endTimeResourceMetrics,
                        startTimeResourceMetrics);
        diskMetric = result.get("disk-1");

        assertEquals(2, diskMetric.utilization, 0d);
        assertEquals(10, diskMetric.await, 0d);
        assertEquals(0.0512, diskMetric.serviceRate, 0d);
    }
}

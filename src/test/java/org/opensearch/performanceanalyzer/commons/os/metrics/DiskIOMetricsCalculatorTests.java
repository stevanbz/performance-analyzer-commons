/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.os.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.opensearch.performanceanalyzer.commons.os.observer.impl.IOObserver.StatKeys;

public class DiskIOMetricsCalculatorTests {

    @Test
    public void testCalculateIOMetrics() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;

        long startNum = 1000;
        long endNum = 2000;

        Map<String, Long> endTimeResourceMetrics = new HashMap<>();
        Map<String, Long> startTimeResourceMetrics = new HashMap<>();

        endTimeResourceMetrics.put(StatKeys.READ_BYTES.getLabel(), endNum);
        startTimeResourceMetrics.put(StatKeys.READ_BYTES.getLabel(), startNum);

        endTimeResourceMetrics.put(StatKeys.WRITE_BYTES.getLabel(), endNum);
        startTimeResourceMetrics.put(StatKeys.WRITE_BYTES.getLabel(), startNum);

        endTimeResourceMetrics.put(StatKeys.SYSCR.getLabel(), endNum);
        startTimeResourceMetrics.put(StatKeys.SYSCR.getLabel(), startNum);

        endTimeResourceMetrics.put(StatKeys.RCHAR.getLabel(), endNum);
        startTimeResourceMetrics.put(StatKeys.RCHAR.getLabel(), startNum);

        endTimeResourceMetrics.put(StatKeys.WCHAR.getLabel(), endNum);
        startTimeResourceMetrics.put(StatKeys.WCHAR.getLabel(), startNum);

        endTimeResourceMetrics.put(StatKeys.SYSCW.getLabel(), endNum);
        startTimeResourceMetrics.put(StatKeys.SYSCW.getLabel(), startNum);

        IOMetrics ioMetrics =
                DiskIOMetricsCalculator.calculateIOMetrics(
                        endTimeInMillis,
                        startTimeInMillis,
                        endTimeResourceMetrics,
                        startTimeResourceMetrics);

        assertEquals(100d, ioMetrics.avgReadThroughputBps, 0);
        assertEquals(100d, ioMetrics.avgWriteThroughputBps, 0);
        assertEquals(200d, ioMetrics.avgTotalThroughputBps, 0);
        assertEquals(100d, ioMetrics.avgReadSyscallRate, 0);
        assertEquals(100d, ioMetrics.avgWriteSyscallRate, 0);
        assertEquals(200d, ioMetrics.avgTotalSyscallRate, 0);
        assertEquals(0d, ioMetrics.avgPageCacheReadThroughputBps, 0);
        assertEquals(0d, ioMetrics.avgPageCacheWriteThroughputBps, 0);
        assertEquals(0d, ioMetrics.avgPageCacheTotalThroughputBps, 0);
    }

    @Test
    public void testCalculateIOMetricsStartTimeSameAsEndTime() {
        long startTimeInMillis = 1553725339;
        IOMetrics ioMetrics =
                DiskIOMetricsCalculator.calculateIOMetrics(
                        startTimeInMillis,
                        startTimeInMillis,
                        Collections.emptyMap(),
                        Collections.emptyMap());
        assertNull(ioMetrics);
    }

    @Test
    public void testCalculateIOMetricsMissingKeys() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;

        Map<String, Long> endTimeResourceMetrics = new HashMap<>();
        Map<String, Long> startTimeResourceMetrics = new HashMap<>();

        IOMetrics ioMetrics =
                DiskIOMetricsCalculator.calculateIOMetrics(
                        endTimeInMillis,
                        startTimeInMillis,
                        endTimeResourceMetrics,
                        Collections.emptyMap());

        long startNum = 1000;
        long endNum = 2000;

        assertEquals(0d, ioMetrics.avgReadThroughputBps, 0);
        assertEquals(0d, ioMetrics.avgWriteThroughputBps, 0);
        assertEquals(0d, ioMetrics.avgTotalThroughputBps, 0);
        assertEquals(0d, ioMetrics.avgReadSyscallRate, 0);
        assertEquals(0d, ioMetrics.avgWriteSyscallRate, 0);
        assertEquals(0d, ioMetrics.avgTotalSyscallRate, 0);
        assertEquals(0d, ioMetrics.avgPageCacheReadThroughputBps, 0);
        assertEquals(0d, ioMetrics.avgPageCacheWriteThroughputBps, 0);
        assertEquals(0d, ioMetrics.avgPageCacheTotalThroughputBps, 0);

        endTimeResourceMetrics.put(StatKeys.READ_BYTES.getLabel(), endNum);
        startTimeResourceMetrics.put(StatKeys.READ_BYTES.getLabel(), startNum);
        endTimeResourceMetrics.put(StatKeys.WRITE_BYTES.getLabel(), endNum);
        startTimeResourceMetrics.put(StatKeys.WRITE_BYTES.getLabel(), startNum);

        ioMetrics =
                DiskIOMetricsCalculator.calculateIOMetrics(
                        endTimeInMillis,
                        startTimeInMillis,
                        endTimeResourceMetrics,
                        startTimeResourceMetrics);

        assertEquals(100d, ioMetrics.avgReadThroughputBps, 0);
        assertEquals(100d, ioMetrics.avgWriteThroughputBps, 0);
        assertEquals(200d, ioMetrics.avgTotalThroughputBps, 0);

        endTimeResourceMetrics.put(StatKeys.SYSCR.getLabel(), endNum);
        startTimeResourceMetrics.put(StatKeys.SYSCR.getLabel(), startNum);

        endTimeResourceMetrics.put(StatKeys.RCHAR.getLabel(), endNum);
        startTimeResourceMetrics.put(StatKeys.RCHAR.getLabel(), startNum);

        endTimeResourceMetrics.put(StatKeys.SYSCW.getLabel(), endNum);
        startTimeResourceMetrics.put(StatKeys.SYSCW.getLabel(), startNum);

        ioMetrics =
                DiskIOMetricsCalculator.calculateIOMetrics(
                        endTimeInMillis,
                        startTimeInMillis,
                        endTimeResourceMetrics,
                        startTimeResourceMetrics);

        assertEquals(100d, ioMetrics.avgReadSyscallRate, 0);
        assertEquals(100d, ioMetrics.avgWriteSyscallRate, 0);
        assertEquals(200d, ioMetrics.avgTotalSyscallRate, 0);
    }
}

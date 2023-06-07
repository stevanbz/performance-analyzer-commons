/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.os.metrics;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensearch.performanceanalyzer.commons.os.OSGlobals;
import org.opensearch.performanceanalyzer.commons.os.observer.impl.CPUObserver.StatKeys;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OSGlobals.class})
@SuppressStaticInitializationFor("org.opensearch.performanceanalyzer.commons.os.OSGlobals")
public class CpuMetricsCalculatorTests {

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(OSGlobals.class);
        PowerMockito.when(OSGlobals.getScClkTck()).thenReturn(100L);
    }

    @Test
    public void testCalculateCPUUtilizationVerifySuccess() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;

        Map<String, Object> endTimeResourceMetrics = new HashMap<>();
        Map<String, Object> startTimeResourceMetrics = new HashMap<>();

        endTimeResourceMetrics.put(StatKeys.UTIME.getLabel(), endTimeInMillis);
        endTimeResourceMetrics.put(StatKeys.STIME.getLabel(), startTimeInMillis);

        startTimeResourceMetrics.put(StatKeys.UTIME.getLabel(), startTimeInMillis);
        startTimeResourceMetrics.put(StatKeys.STIME.getLabel(), startTimeInMillis);

        double result =
                CPUMetricsCalculator.calculateCPUUtilization(
                        endTimeInMillis,
                        startTimeInMillis,
                        endTimeResourceMetrics,
                        startTimeResourceMetrics);

        assertEquals(10d, result, 0);
    }

    @Test
    public void testCalculateCPUUtilizationMissingUTimeAndSTimeKeys() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;

        double result =
                CPUMetricsCalculator.calculateCPUUtilization(
                        endTimeInMillis,
                        startTimeInMillis,
                        Collections.emptyMap(),
                        Collections.emptyMap());

        assertEquals(0d, result, 0);
    }

    @Test
    public void testCalculateCPUUtilizationStartTimeSameAsEndTime() {
        long startTimeInMillis = 1553725339;

        double result =
                CPUMetricsCalculator.calculateCPUUtilization(
                        startTimeInMillis,
                        startTimeInMillis,
                        Collections.emptyMap(),
                        Collections.emptyMap());

        assertEquals(0d, result, 0);
    }

    @Test
    public void testCalculateMajorFault() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;

        Map<String, Object> endTimeResourceMetrics = new HashMap<>();
        Map<String, Object> startTimeResourceMetrics = new HashMap<>();

        endTimeResourceMetrics.put(StatKeys.MAJFLT.getLabel(), endTimeInMillis);
        startTimeResourceMetrics.put(StatKeys.MAJFLT.getLabel(), startTimeInMillis);

        double result =
                CPUMetricsCalculator.calculateMajorFault(
                        endTimeInMillis,
                        startTimeInMillis,
                        endTimeResourceMetrics,
                        startTimeResourceMetrics);

        assertEquals(1000d, result, 0);
    }

    @Test
    public void testCalculateMajorFaultMissingMajorFaultKey() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;

        double result =
                CPUMetricsCalculator.calculateMajorFault(
                        endTimeInMillis,
                        startTimeInMillis,
                        Collections.emptyMap(),
                        Collections.emptyMap());

        assertEquals(0d, result, 0);
    }

    @Test
    public void testCalculateMajorFaultStartTimeSameAsEndTime() {
        long startTimeInMillis = 1553725339;

        double result =
                CPUMetricsCalculator.calculateMajorFault(
                        startTimeInMillis,
                        startTimeInMillis,
                        Collections.emptyMap(),
                        Collections.emptyMap());

        assertEquals(0d, result, 0);
    }

    @Test
    public void testCalculateMinorFault() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;

        Map<String, Object> endTimeResourceMetrics = new HashMap<>();
        Map<String, Object> startTimeResourceMetrics = new HashMap<>();

        endTimeResourceMetrics.put(StatKeys.MINFLT.getLabel(), endTimeInMillis);
        startTimeResourceMetrics.put(StatKeys.MINFLT.getLabel(), startTimeInMillis);

        double result =
                CPUMetricsCalculator.calculateMinorFault(
                        endTimeInMillis,
                        startTimeInMillis,
                        endTimeResourceMetrics,
                        startTimeResourceMetrics);

        assertEquals(1000d, result, 0);
    }

    @Test
    public void testCalculateMinorFaultMissingMinorFaultKey() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;

        double result =
                CPUMetricsCalculator.calculateMinorFault(
                        endTimeInMillis,
                        startTimeInMillis,
                        Collections.emptyMap(),
                        Collections.emptyMap());

        assertEquals(0d, result, 0);
    }

    @Test
    public void testCalculateMinorFaultStartTimeSameAsEndTime() {
        long startTimeInMillis = 1553725339;

        double result =
                CPUMetricsCalculator.calculateMinorFault(
                        startTimeInMillis,
                        startTimeInMillis,
                        Collections.emptyMap(),
                        Collections.emptyMap());

        assertEquals(0d, result, 0);
    }

    @Test
    public void testCalculateMinorFaultStartTimeAfterEndTime() {
        long startTimeInMillis = 1553735339;
        long endTimeInMillis = 1553725339;

        Map<String, Object> endTimeResourceMetrics = new HashMap<>();
        Map<String, Object> startTimeResourceMetrics = new HashMap<>();

        endTimeResourceMetrics.put(StatKeys.UTIME.getLabel(), endTimeInMillis);
        endTimeResourceMetrics.put(StatKeys.STIME.getLabel(), startTimeInMillis);

        startTimeResourceMetrics.put(StatKeys.UTIME.getLabel(), startTimeInMillis);
        startTimeResourceMetrics.put(StatKeys.STIME.getLabel(), startTimeInMillis);

        double result =
                CPUMetricsCalculator.calculateMinorFault(
                        endTimeInMillis,
                        startTimeInMillis,
                        endTimeResourceMetrics,
                        startTimeResourceMetrics);
        assertEquals(0d, result, 0);
    }
}

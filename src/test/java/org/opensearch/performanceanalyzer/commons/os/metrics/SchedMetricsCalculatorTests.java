/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.os.metrics;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.opensearch.performanceanalyzer.commons.os.observer.impl.SchedObserver.SchedKeys;

public class SchedMetricsCalculatorTests {

    @Test
    public void testCalculateThreadSchedLatency() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;

        long startNum = 1000;
        long endNum = 1001;

        Map<String, Object> endTimeResourceMetrics = new HashMap<>();
        Map<String, Object> startTimeResourceMetrics = new HashMap<>();

        endTimeResourceMetrics.put(SchedKeys.TOTCTXSWS.getLabel(), endNum);
        startTimeResourceMetrics.put(SchedKeys.TOTCTXSWS.getLabel(), startNum);

        endTimeResourceMetrics.put(SchedKeys.RUNTICKS.getLabel(), endNum);
        startTimeResourceMetrics.put(SchedKeys.RUNTICKS.getLabel(), startNum);

        endTimeResourceMetrics.put(SchedKeys.WAITTICKS.getLabel(), endNum);
        startTimeResourceMetrics.put(SchedKeys.WAITTICKS.getLabel(), startNum);

        SchedMetrics result =
                SchedMetricsCalculator.calculateThreadSchedLatency(
                        endTimeInMillis,
                        startTimeInMillis,
                        endTimeResourceMetrics,
                        startTimeResourceMetrics);

        assertNotNull(result);
        assertEquals(1.0E-9, result.avgRuntime, 0);
        assertEquals(1.0E-9, result.avgWaittime, 0);
        assertEquals(0.1, result.contextSwitchRate, 0);
    }

    @Test
    public void testCalculateThreadSchedLatencyStartTimeSameAsEndTime() {
        long startTimeInMillis = 1553725339;

        SchedMetrics result =
                SchedMetricsCalculator.calculateThreadSchedLatency(
                        startTimeInMillis,
                        startTimeInMillis,
                        Collections.emptyMap(),
                        Collections.emptyMap());

        assertNull(result);
    }

    @Test
    public void testCalculateThreadSchedLatencyMissingTOTCTXSWSKey() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;

        SchedMetrics result =
                SchedMetricsCalculator.calculateThreadSchedLatency(
                        startTimeInMillis,
                        endTimeInMillis,
                        Collections.singletonMap(SchedKeys.RUNTICKS.getLabel(), 1),
                        Collections.singletonMap(SchedKeys.RUNTICKS.getLabel(), 1));

        assertNull(result);
    }

    @Test
    public void testCalculateThreadSchedLatencyNullMap() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;

        SchedMetrics result =
                SchedMetricsCalculator.calculateThreadSchedLatency(
                        startTimeInMillis, endTimeInMillis, null, null);

        assertNull(result);
    }
}

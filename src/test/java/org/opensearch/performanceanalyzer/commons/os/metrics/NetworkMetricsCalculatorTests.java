/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.os.metrics;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import org.opensearch.performanceanalyzer.commons.collectors.NetInterfaceSummary;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.impl.DeviceNetworkStatsObserver;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.impl.Ipv4Observer;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.impl.Ipv6Observer;

public class NetworkMetricsCalculatorTests {

    @Test
    public void testCalculateInNetworkMetrics() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;
        long startNum = 1000;
        long endNum = 2000;

        Map<String, Long> curipv4 = Map.of(
                Ipv4Observer.Ipv4Keys.IN_RECEIVES.getLabel(),
                endNum,
                Ipv4Observer.Ipv4Keys.IN_DELIVERS.getLabel(),
                endNum
        );
        Map<String, Long> oldipv4 = Map.of(
                Ipv4Observer.Ipv4Keys.IN_RECEIVES.getLabel(),
                startNum,
                Ipv4Observer.Ipv4Keys.IN_DELIVERS.getLabel(),
                startNum
        );
        Map<String, Long> curipv6 = Map.of(
                Ipv6Observer.Ipv6Keys.IN_RECEIVES.getLabel(),
                endNum,
                Ipv6Observer.Ipv6Keys.IN_DELIVERS.getLabel(),
                endNum
        );
        Map<String, Long> oldpv6 = Map.of(
                Ipv6Observer.Ipv6Keys.IN_RECEIVES.getLabel(),
                startNum,
                Ipv6Observer.Ipv6Keys.IN_DELIVERS.getLabel(),
                startNum
        );
        Map<String, Long> curphy = Collections.singletonMap(DeviceNetworkStatsObserver.NetworkStatKeys.IN_BYTES.getLabel(), endNum);
        Map<String, Long> oldphy = Collections.singletonMap(DeviceNetworkStatsObserver.NetworkStatKeys.IN_BYTES.getLabel(), startNum);
        NetInterfaceSummary result = NetworkMetricsCalculator.calculateInNetworkMetrics(
                endTimeInMillis,
                startTimeInMillis,
                curipv4,
                oldipv4,
                curipv6,
                oldpv6,
                curphy,
                oldphy
        );

        assertNotNull(result);
        assertEquals(result.getDirection(), NetInterfaceSummary.Direction.in);
        assertEquals(100d, result.getPacketRate4(), 0d);
        assertEquals(100d, result.getPacketRate6(), 0d);
        assertEquals(0d, result.getDropRate4(), 0d);
        assertEquals(0d, result.getDropRate6(), 0d);
        assertEquals(800d, result.getBps(), 0d);
    }

    @Test
    public void testCalculateOutNetworkMetrics() {
        long startTimeInMillis = 1553725339;
        long endTimeInMillis = 1553735339;
        long startNum = 1000;
        long endNum = 2000;

        Map<String, Long> curipv4 = Map.of(
                Ipv4Observer.Ipv4Keys.OUT_REQUESTS.getLabel(), endNum,
                Ipv4Observer.Ipv4Keys.OUT_DISCARDS.getLabel(), endNum,
                Ipv4Observer.Ipv4Keys.OUT_NO_ROUTES.getLabel(), endNum
        );
        Map<String, Long> oldipv4 = Map.of(
                Ipv4Observer.Ipv4Keys.OUT_REQUESTS.getLabel(), startNum,
                Ipv4Observer.Ipv4Keys.OUT_DISCARDS.getLabel(), startNum,
                Ipv4Observer.Ipv4Keys.OUT_NO_ROUTES.getLabel(), startNum
        );

        Map<String, Long> curipv6 = Map.of(
                Ipv6Observer.Ipv6Keys.OUT_REQUESTS.getLabel(), endNum,
                Ipv6Observer.Ipv6Keys.OUT_DISCARDS.getLabel(), endNum,
                Ipv6Observer.Ipv6Keys.OUT_NO_ROUTES.getLabel(), endNum
        );
        Map<String, Long> oldpv6 = Map.of(
                Ipv6Observer.Ipv6Keys.OUT_REQUESTS.getLabel(), startNum,
                Ipv6Observer.Ipv6Keys.OUT_DISCARDS.getLabel(), startNum,
                Ipv6Observer.Ipv6Keys.OUT_NO_ROUTES.getLabel(), startNum
        );

        Map<String, Long> curphy = Collections.singletonMap(DeviceNetworkStatsObserver.NetworkStatKeys.OUT_BYTES.getLabel(), endNum);
        Map<String, Long> oldphy = Collections.singletonMap(DeviceNetworkStatsObserver.NetworkStatKeys.OUT_BYTES.getLabel(), startNum);
        NetInterfaceSummary result = NetworkMetricsCalculator.calculateOutNetworkMetrics(
                endTimeInMillis,
                startTimeInMillis,
                curipv4,
                oldipv4,
                curipv6,
                oldpv6,
                curphy,
                oldphy
        );

        assertNotNull(result);
        assertEquals(result.getDirection(), NetInterfaceSummary.Direction.out);
        assertEquals(100d, result.getPacketRate4(), 0d);
        assertEquals(200d, result.getDropRate4(), 0d);
        assertEquals(100d, result.getPacketRate6(), 0d);
        assertEquals(200d, result.getDropRate6(), 0d);
        assertEquals(200d, result.getDropRate6(), 0d);
        assertEquals(800d, result.getBps(), 0d);
    }

    @Test
    public void testCalculateInNetworkMetricsStartTimeAfterEndTime() {
        long startTimeInMillis = 1553735339;
        long endTimeInMillis = 1553725339;
        long startNum = 1000;
        long endNum = 2000;

        Map<String, Long> curipv4 = Map.of(
                Ipv4Observer.Ipv4Keys.IN_RECEIVES.getLabel(),
                endNum,
                Ipv4Observer.Ipv4Keys.IN_DELIVERS.getLabel(),
                endNum
        );
        Map<String, Long> oldipv4 = Map.of(
                Ipv4Observer.Ipv4Keys.IN_RECEIVES.getLabel(),
                startNum,
                Ipv4Observer.Ipv4Keys.IN_DELIVERS.getLabel(),
                startNum
        );
        Map<String, Long> curipv6 = Map.of(
                Ipv6Observer.Ipv6Keys.IN_RECEIVES.getLabel(),
                endNum,
                Ipv6Observer.Ipv6Keys.IN_DELIVERS.getLabel(),
                endNum
        );
        Map<String, Long> oldpv6 = Map.of(
                Ipv6Observer.Ipv6Keys.IN_RECEIVES.getLabel(),
                startNum,
                Ipv6Observer.Ipv6Keys.IN_DELIVERS.getLabel(),
                startNum
        );
        Map<String, Long> curphy = Collections.singletonMap(DeviceNetworkStatsObserver.NetworkStatKeys.IN_BYTES.getLabel(), endNum);
        Map<String, Long> oldphy = Collections.singletonMap(DeviceNetworkStatsObserver.NetworkStatKeys.IN_BYTES.getLabel(), startNum);
        NetInterfaceSummary result = NetworkMetricsCalculator.calculateInNetworkMetrics(
                endTimeInMillis,
                startTimeInMillis,
                curipv4,
                oldipv4,
                curipv6,
                oldpv6,
                curphy,
                oldphy
        );

        assertNull(result);
    }

    @Test
    public void testCalculateOutNetworkMetricsStartTimeAfterEndTime() {
        long startTimeInMillis = 1553735339;
        long endTimeInMillis = 1553725339;
        long startNum = 1000;
        long endNum = 2000;

        Map<String, Long> curipv4 = Map.of(
                Ipv4Observer.Ipv4Keys.OUT_REQUESTS.getLabel(), endNum,
                Ipv4Observer.Ipv4Keys.OUT_DISCARDS.getLabel(), endNum,
                Ipv4Observer.Ipv4Keys.OUT_NO_ROUTES.getLabel(), endNum
        );
        Map<String, Long> oldipv4 = Map.of(
                Ipv4Observer.Ipv4Keys.OUT_REQUESTS.getLabel(), startNum,
                Ipv4Observer.Ipv4Keys.OUT_DISCARDS.getLabel(), startNum,
                Ipv4Observer.Ipv4Keys.OUT_NO_ROUTES.getLabel(), startNum
        );

        Map<String, Long> curipv6 = Map.of(
                Ipv6Observer.Ipv6Keys.OUT_REQUESTS.getLabel(), endNum,
                Ipv6Observer.Ipv6Keys.OUT_DISCARDS.getLabel(), endNum,
                Ipv6Observer.Ipv6Keys.OUT_NO_ROUTES.getLabel(), endNum
        );
        Map<String, Long> oldpv6 = Map.of(
                Ipv6Observer.Ipv6Keys.OUT_REQUESTS.getLabel(), startNum,
                Ipv6Observer.Ipv6Keys.OUT_DISCARDS.getLabel(), startNum,
                Ipv6Observer.Ipv6Keys.OUT_NO_ROUTES.getLabel(), startNum
        );

        Map<String, Long> curphy = Collections.singletonMap(DeviceNetworkStatsObserver.NetworkStatKeys.OUT_BYTES.getLabel(), endNum);
        Map<String, Long> oldphy = Collections.singletonMap(DeviceNetworkStatsObserver.NetworkStatKeys.OUT_BYTES.getLabel(), startNum);
        NetInterfaceSummary result = NetworkMetricsCalculator.calculateOutNetworkMetrics(
                endTimeInMillis,
                startTimeInMillis,
                curipv4,
                oldipv4,
                        curipv6,
                        oldpv6,
                        curphy,
                        oldphy);

        assertNull(result);
    }
}

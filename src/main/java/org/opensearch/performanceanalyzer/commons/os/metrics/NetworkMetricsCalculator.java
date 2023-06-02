/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.os.metrics;


import java.util.Map;
import org.opensearch.performanceanalyzer.commons.collectors.NetInterfaceSummary;

public class NetworkMetricsCalculator {

    public static NetInterfaceSummary calculateInNetworkMetrics(
            long endMeasurementTime,
            long startMeasurementTime,
            Map<String, Long> curipv4,
            Map<String, Long> oldipv4,
            Map<String, Long> curipv6,
            Map<String, Long> oldpv6,
            Map<String, Long> curphy,
            Map<String, Long> oldphy) {

        if (endMeasurementTime <= startMeasurementTime) {
            return null;
        }
        long nin = curipv4.get("InReceives") - oldipv4.get("InReceives");
        long delivin = curipv4.get("InDelivers") - oldipv4.get("InDelivers");
        long nin6 = curipv6.get("Ip6InReceives") - oldpv6.get("Ip6InReceives");
        long delivin6 = curipv6.get("Ip6InDelivers") - oldpv6.get("Ip6InDelivers");
        long timeDelta = endMeasurementTime - startMeasurementTime;
        double inbps = 8 * 1.0e3 * (curphy.get("inbytes") - oldphy.get("inbytes")) / timeDelta;
        double inPacketRate4 = 1.0e3 * (nin) / timeDelta;
        double inDropRate4 = 1.0e3 * (nin - delivin) / timeDelta;
        double inPacketRate6 = 1.0e3 * (nin6) / timeDelta;
        double inDropRate6 = 1.0e3 * (nin6 - delivin6) / timeDelta;

        return new NetInterfaceSummary(
                NetInterfaceSummary.Direction.in,
                inPacketRate4,
                inDropRate4,
                inPacketRate6,
                inDropRate6,
                inbps);
    }

    public static NetInterfaceSummary calculateOutNetworkMetrics(
            long endMeasurementTime,
            long startMeasurementTime,
            Map<String, Long> curipv4,
            Map<String, Long> oldipv4,
            Map<String, Long> curipv6,
            Map<String, Long> oldpv6,
            Map<String, Long> curphy,
            Map<String, Long> oldphy) {
        if (endMeasurementTime <= startMeasurementTime) {
            return null;
        }

        long nout = curipv4.get("OutRequests") - oldipv4.get("OutRequests");
        long dropout =
                curipv4.get("OutDiscards")
                        + curipv4.get("OutNoRoutes")
                        - oldipv4.get("OutDiscards")
                        - oldipv4.get("OutNoRoutes");
        long nout6 = curipv6.get("Ip6OutRequests") - oldpv6.get("Ip6OutRequests");
        long dropout6 =
                curipv6.get("Ip6OutDiscards")
                        + curipv6.get("Ip6OutNoRoutes")
                        - oldpv6.get("Ip6OutDiscards")
                        - oldpv6.get("Ip6OutNoRoutes");
        long timeDelta = endMeasurementTime - startMeasurementTime;
        double outbps = 8 * 1.0e3 * (curphy.get("outbytes") - oldphy.get("outbytes")) / timeDelta;
        double outPacketRate4 = 1.0e3 * (nout) / timeDelta;
        double outDropRate4 = 1.0e3 * (dropout) / timeDelta;
        double outPacketRate6 = 1.0e3 * (nout6) / timeDelta;
        double outDropRate6 = 1.0e3 * (dropout6) / timeDelta;

        return new NetInterfaceSummary(
                NetInterfaceSummary.Direction.out,
                outPacketRate4,
                outDropRate4,
                outPacketRate6,
                outDropRate6,
                outbps);
    }
}

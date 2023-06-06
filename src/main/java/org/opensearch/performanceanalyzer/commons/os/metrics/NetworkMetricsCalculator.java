/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.os.metrics;


import java.util.Map;
import org.opensearch.performanceanalyzer.commons.collectors.NetInterfaceSummary;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.impl.DeviceNetworkStatsObserver;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.impl.Ipv4Observer;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.impl.Ipv6Observer;

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
        long nin = curipv4.get(Ipv4Observer.Ipv4Keys.IN_RECEIVES.getLabel())
                - oldipv4.get(Ipv4Observer.Ipv4Keys.IN_RECEIVES.getLabel());
        long delivin = curipv4.get(Ipv4Observer.Ipv4Keys.IN_DELIVERS.getLabel())
                - oldipv4.get(Ipv4Observer.Ipv4Keys.IN_DELIVERS.getLabel());
        long nin6 = curipv6.get(Ipv6Observer.Ipv6Keys.IN_RECEIVES.getLabel())
                - oldpv6.get(Ipv6Observer.Ipv6Keys.IN_RECEIVES.getLabel());
        long delivin6 = curipv6.get(Ipv6Observer.Ipv6Keys.IN_DELIVERS.getLabel())
                - oldpv6.get(Ipv6Observer.Ipv6Keys.IN_DELIVERS.getLabel());
        long timeDelta = endMeasurementTime - startMeasurementTime;
        double inbps = 8 * 1.0e3 * (curphy.get(DeviceNetworkStatsObserver.NetworkStatKeys.IN_BYTES.getLabel())
                - oldphy.get(DeviceNetworkStatsObserver.NetworkStatKeys.IN_BYTES.getLabel())) / timeDelta;
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

        long nout =
                curipv4.get(Ipv4Observer.Ipv4Keys.OUT_REQUESTS.getLabel())
                        - oldipv4.get(Ipv4Observer.Ipv4Keys.OUT_REQUESTS.getLabel());
        long dropout = curipv4.get(Ipv4Observer.Ipv4Keys.OUT_DISCARDS.getLabel())
                + curipv4.get(Ipv4Observer.Ipv4Keys.OUT_NO_ROUTES.getLabel())
                - oldipv4.get(Ipv4Observer.Ipv4Keys.OUT_DISCARDS.getLabel())
                - oldipv4.get(Ipv4Observer.Ipv4Keys.OUT_NO_ROUTES.getLabel());
        long nout6 = curipv6.get(Ipv6Observer.Ipv6Keys.OUT_REQUESTS.getLabel())
                - oldpv6.get(Ipv6Observer.Ipv6Keys.OUT_REQUESTS.getLabel());
        long dropout6 = curipv6.get(Ipv6Observer.Ipv6Keys.OUT_DISCARDS.getLabel())
                + curipv6.get(Ipv6Observer.Ipv6Keys.OUT_NO_ROUTES.getLabel())
                - oldpv6.get(Ipv6Observer.Ipv6Keys.OUT_DISCARDS.getLabel())
                - oldpv6.get(Ipv6Observer.Ipv6Keys.OUT_NO_ROUTES.getLabel());
        long timeDelta = endMeasurementTime - startMeasurementTime;
        double outbps = 8 * 1.0e3 * (curphy.get(DeviceNetworkStatsObserver.NetworkStatKeys.OUT_BYTES.getLabel())
                - oldphy.get(DeviceNetworkStatsObserver.NetworkStatKeys.OUT_BYTES.getLabel())) / timeDelta;
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

/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.hwnet;


import com.google.common.annotations.VisibleForTesting;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.performanceanalyzer.commons.collectors.NetInterfaceSummary;
import org.opensearch.performanceanalyzer.commons.metrics_generator.linux.LinuxIPMetricsGenerator;
import org.opensearch.performanceanalyzer.commons.observer.ResourceObserver;
import org.opensearch.performanceanalyzer.commons.os.metrics.NetworkMetricsCalculator;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.impl.DeviceNetworkStatsObserver;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.impl.Ipv4Observer;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.impl.Ipv6Observer;

public class NetworkInterface {
    private static final Logger LOG = LogManager.getLogger(NetworkInterface.class);

    /* Data sources:
     /proc/net/snmp, /prov/net/snmp6, /proc/net/dev
     measures tcp and ip-layer pathologies.
     SNMP fields of interest (see RFCs 2011 and 1213):
     - [ip6]inReceives: total including errors
     - [ip6]inDelivers: sent to next layer (including ICMP)
     - [ip6]outRequests: sent from previous layer
     - [ip6]outDiscards + [ip6]outNoRoutes: sender-side drops
    */

    static class NetInterfaceMetrics {
        Map<String, Long> PHYmetrics = new HashMap<>();
        Map<String, Long> IPmetrics = new HashMap<>();
        // these three are currently unused;
        // leaving them commented for now.
        /*Map<String, Long> TCPmetrics =
            new HashMap<>();
        Map<String, Long> UDPmetrics =
            new HashMap<>();
        Map<String, Long> ICMPmetrics =
            new HashMap<>();*/

        public void clearAll() {
            PHYmetrics.clear();
            IPmetrics.clear();
            /*TCPmetrics.clear();
            UDPmetrics.clear();
            ICMPmetrics.clear();*/
        }

        public void putAll(NetInterfaceMetrics m) {
            PHYmetrics.putAll(m.PHYmetrics);
            IPmetrics.putAll(m.IPmetrics);
            /*TCPmetrics.putAll(m.TCPmetrics);
            UDPmetrics.putAll(m.UDPmetrics);
            ICMPmetrics.putAll(m.ICMPmetrics);*/
        }
    }

    private static NetInterfaceMetrics currentMetrics = new NetInterfaceMetrics();
    private static NetInterfaceMetrics oldMetrics = new NetInterfaceMetrics();
    private static Map<String, Long> currentMetrics6 = new HashMap<>();

    private static ResourceObserver ipv4Observer = new Ipv4Observer();

    private static ResourceObserver ipv6Observer = new Ipv6Observer();

    private static ResourceObserver deviceNetworkStatsObserver = new DeviceNetworkStatsObserver();
    private static Map<String, Long> oldMetrics6 = new HashMap<>();
    private static long kvTimestamp = 0;
    private static long oldkvTimestamp = 0;

    private static StringBuilder ret = new StringBuilder();

    private static String[] IPkeys = null;
    //    static private String[] TCPkeys = null;
    //    static private String[] UDPkeys = null;
    //    static private String[] ICMPkeys = null;

    private static LinuxIPMetricsGenerator linuxIPMetricsGenerator = new LinuxIPMetricsGenerator();

    static {
        addSampleHelper();
    }

    public static LinuxIPMetricsGenerator getLinuxIPMetricsGenerator() {
        return linuxIPMetricsGenerator;
    }

    protected static void calculateNetworkMetrics() {
        if (kvTimestamp <= oldkvTimestamp) {
            linuxIPMetricsGenerator.setInNetworkInterfaceSummary(null);
            linuxIPMetricsGenerator.setOutNetworkInterfaceSummary(null);
            return;
        }

        NetInterfaceSummary inNetwork =
                NetworkMetricsCalculator.calculateInNetworkMetrics(
                        kvTimestamp,
                        oldkvTimestamp,
                        currentMetrics.IPmetrics,
                        oldMetrics.IPmetrics,
                        currentMetrics6,
                        oldMetrics6,
                        currentMetrics.PHYmetrics,
                        oldMetrics.PHYmetrics);

        NetInterfaceSummary outNetwork =
                NetworkMetricsCalculator.calculateOutNetworkMetrics(
                        kvTimestamp,
                        oldkvTimestamp,
                        currentMetrics.IPmetrics,
                        oldMetrics.IPmetrics,
                        currentMetrics6,
                        oldMetrics6,
                        currentMetrics.PHYmetrics,
                        oldMetrics.PHYmetrics);

        linuxIPMetricsGenerator.setInNetworkInterfaceSummary(inNetwork);
        linuxIPMetricsGenerator.setOutNetworkInterfaceSummary(outNetwork);
    }

    private static void addSample4() {
        oldMetrics.clearAll();
        oldMetrics.putAll(currentMetrics);
        currentMetrics.clearAll();
        oldkvTimestamp = kvTimestamp;
        kvTimestamp = System.currentTimeMillis();
        currentMetrics.IPmetrics.putAll(ipv4Observer.observe());
    }

    private static void addSample6() {
        oldMetrics6.clear();
        oldMetrics6.putAll(currentMetrics6);
        currentMetrics6.clear();
        currentMetrics6.putAll(ipv6Observer.observe());
    }

    // this assumes that addSample4() is called
    private static void addDeviceStats() {
        currentMetrics.PHYmetrics.putAll(deviceNetworkStatsObserver.observe());
    }

    public static void addSample() {
        addSampleHelper();
        calculateNetworkMetrics();
    }

    private static synchronized void addSampleHelper() {
        addSample4();
        addSample6();
        addDeviceStats();
    }

    public static void runOnce() {
        addSample();
    }

    @VisibleForTesting
    Map<String, Long> getCurrentPhyMetric() {
        return currentMetrics.PHYmetrics;
    }

    @VisibleForTesting
    Map<String, Long> getCurrentIpMetric() {
        return currentMetrics.IPmetrics;
    }

    @VisibleForTesting
    Map<String, Long> getOldPhyMetric() {
        return oldMetrics.PHYmetrics;
    }

    @VisibleForTesting
    Map<String, Long> getOldIpMetric() {
        return oldMetrics.IPmetrics;
    }

    @VisibleForTesting
    Map<String, Long> getCurrentMetrics6() {
        return currentMetrics6;
    }

    @VisibleForTesting
    Map<String, Long> getOldMetrics6() {
        return oldMetrics6;
    }

    @VisibleForTesting
    void putCurrentPhyMetric(String key, Long value) {
        currentMetrics.PHYmetrics.put(key, value);
    }

    @VisibleForTesting
    void putCurrentIpMetric(String key, Long value) {
        currentMetrics.IPmetrics.put(key, value);
    }

    @VisibleForTesting
    void putOldPhyMetric(String key, Long value) {
        oldMetrics.PHYmetrics.put(key, value);
    }

    @VisibleForTesting
    void putOldIpMetric(String key, Long value) {
        oldMetrics.IPmetrics.put(key, value);
    }

    @VisibleForTesting
    void putCurrentMetrics6(String key, Long value) {
        currentMetrics6.put(key, value);
    }

    @VisibleForTesting
    void putOldMetrics6(String key, Long value) {
        oldMetrics6.put(key, value);
    }

    @VisibleForTesting
    static void setKvTimestamp(long value) {
        NetworkInterface.kvTimestamp = value;
    }

    @VisibleForTesting
    static void setOldkvTimestamp(long oldkvTimestamp) {
        NetworkInterface.oldkvTimestamp = oldkvTimestamp;
    }

    @VisibleForTesting
    static long getKvTimestamp() {
        return kvTimestamp;
    }

    @VisibleForTesting
    static long getOldkvTimestamp() {
        return oldkvTimestamp;
    }
}

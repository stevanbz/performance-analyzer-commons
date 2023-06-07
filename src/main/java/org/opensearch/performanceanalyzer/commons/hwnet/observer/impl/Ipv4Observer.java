/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.hwnet.observer.impl;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.performanceanalyzer.commons.collectors.StatsCollector;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.NetObserver;
import org.opensearch.performanceanalyzer.commons.stats.metrics.StatExceptionCode;

public class Ipv4Observer extends NetObserver<Long> {

    public enum Ipv4Keys {
        IN_RECEIVES("InReceives"),
        IN_DELIVERS("InDelivers"),
        OUT_REQUESTS("OutRequests"),
        OUT_DISCARDS("OutDiscards"),
        OUT_NO_ROUTES("OutNoRoutes");
        private final String label;

        public String getLabel() {
            return label;
        }

        Ipv4Keys(String label) {
            this.label = label;
        }
    }

    private static final Logger LOG = LogManager.getLogger(Ipv4Observer.class);

    @Override
    public Long observe(String threadId) {
        throw new UnsupportedOperationException(
                "IP observer is not supporting observing the ip params by thread id");
    }

    @Override
    public Map<String, Long> observe() {
        int ln = 0;
        String[] ipKeys = null;
        Map<String, Long> ipMetrics = new HashMap<>();
        try (FileReader fileReader = new FileReader(new File("/proc/net/snmp"));
                BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (ln % 2 == 0) { // keys
                    if (ipKeys == null && line.startsWith("Ip:")) {
                        ipKeys = line.split("\\s+");
                    }
                } else {
                    if (ipKeys != null) {
                        String[] values = line.split("\\s+");
                        int count = values.length;
                        ipMetrics.put(ipKeys[0], 0L);
                        for (int i = 1; i < count; i++) {
                            ipMetrics.put(ipKeys[i], Long.parseLong(values[i]));
                        }
                    }
                }
                ln++;
            }
        } catch (Exception e) {
            LOG.debug(
                    "Exception in calling addSample4 with details: {} with ExceptionCode: {}",
                    () -> e.toString(),
                    () -> StatExceptionCode.NETWORK_COLLECTION_ERROR.toString());
            StatsCollector.instance().logException(StatExceptionCode.NETWORK_COLLECTION_ERROR);
        }
        return ipMetrics;
    }
}

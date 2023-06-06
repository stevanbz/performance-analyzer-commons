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
import org.opensearch.performanceanalyzer.commons.observer.ResourceObserver;
import org.opensearch.performanceanalyzer.commons.stats.metrics.StatExceptionCode;

public class DeviceNetworkStatsObserver implements ResourceObserver<Long> {

    public enum NetworkStatKeys {
        IN_BYTES("inbytes"),
        OUT_BYTES("outbytes");
        private final String label;

        public String getLabel() {
            return label;
        }

        NetworkStatKeys(String label) {
            this.label = label;
        }
    }

    private static final Logger LOG = LogManager.getLogger(DeviceNetworkStatsObserver.class);

    @Override
    public Long observe(String threadId) {
        throw new UnsupportedOperationException(
                "Device stats observer is not supporting observing the stats by thread id");
    }

    @Override
    public Map<String, Long> observe() {
        Map<String, Long> phyMetrics = new HashMap<>();
        try (FileReader fileReader = new FileReader(new File("/proc/net/dev"));
                BufferedReader bufferedReader = new BufferedReader(fileReader); ) {
            String line;
            long intotbytes = 0;
            long outtotbytes = 0;
            long intotpackets = 0;
            long outtotpackets = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("Receive") || line.contains("packets")) {
                    continue;
                }
                String[] toks = line.trim().split(" +");
                intotbytes += Long.parseLong(toks[1]);
                intotpackets += Long.parseLong(toks[2]);
                outtotbytes += Long.parseLong(toks[9]);
                outtotpackets += Long.parseLong(toks[10]);
            }
            phyMetrics.put("inbytes", intotbytes);
            phyMetrics.put("inpackets", intotpackets);
            phyMetrics.put("outbytes", outtotbytes);
            phyMetrics.put("outpackets", outtotpackets);
        } catch (Exception e) {
            LOG.debug(
                    "Exception in calling addDeviceStats with details: {} with ExceptionCode: {}",
                    () -> e.toString(),
                    () -> StatExceptionCode.NETWORK_COLLECTION_ERROR.toString());
            StatsCollector.instance().logException(StatExceptionCode.NETWORK_COLLECTION_ERROR);
        }
        return phyMetrics;
    }
}

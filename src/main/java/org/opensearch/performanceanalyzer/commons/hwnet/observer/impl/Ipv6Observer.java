/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.hwnet.observer.impl;


import com.google.common.base.Splitter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.performanceanalyzer.commons.collectors.StatsCollector;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.NetObserver;
import org.opensearch.performanceanalyzer.commons.stats.metrics.StatExceptionCode;

public class Ipv6Observer extends NetObserver<Long> {

    public enum Ipv6Keys {
        IN_RECEIVES("Ip6InReceives"),
        IN_DELIVERS("Ip6InDelivers"),
        OUT_REQUESTS("Ip6OutRequests"),
        OUT_DISCARDS("Ip6OutDiscards"),
        OUT_NO_ROUTES("Ip6OutNoRoutes");
        private final String label;

        public String getLabel() {
            return label;
        }

        Ipv6Keys(String label) {
            this.label = label;
        }
    }

    private static final Logger LOG = LogManager.getLogger(Ipv6Observer.class);
    private static final Splitter STRING_PATTERN_SPLITTER = Splitter.on(Pattern.compile("[ \\t]+"));

    @Override
    public Long observe(String threadId) {
        throw new UnsupportedOperationException(
                "Network observer is not supporting observing the resource by thread id");
    }

    @Override
    public Map<String, Long> observe() {
        Map<String, Long> metrics6 = new HashMap<>();
        try (FileReader fileReader = new FileReader(new File("/proc/net/snmp6"));
                BufferedReader bufferedReader = new BufferedReader(fileReader); ) {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                List<String> toks = STRING_PATTERN_SPLITTER.splitToList(line);
                if (toks.size() > 1) {
                    metrics6.put(toks.get(0), Long.parseLong(toks.get(1)));
                }
            }
        } catch (Exception e) {
            LOG.debug(
                    "Exception in calling addSample6 with details: {} with ExceptionCode: {}",
                    () -> e.toString(),
                    () -> StatExceptionCode.NETWORK_COLLECTION_ERROR.toString());
            StatsCollector.instance().logException(StatExceptionCode.NETWORK_COLLECTION_ERROR);
        }
        return metrics6;
    }
}

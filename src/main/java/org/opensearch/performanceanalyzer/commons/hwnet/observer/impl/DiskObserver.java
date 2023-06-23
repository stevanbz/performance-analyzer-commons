/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.hwnet.observer.impl;


import java.io.File;
import java.util.*;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.NetObserver;
import org.opensearch.performanceanalyzer.commons.os.SchemaFileParser;
import org.opensearch.performanceanalyzer.commons.util.Util;

public class DiskObserver extends NetObserver<Map<String, Object>> {
    public enum DiskKeys {
        MAJNO("majno"),
        MINNO("minno"),
        NAME("name"),
        RDONE("rdone"),
        RMERGED("rmerged"),
        RSECTORS("rsectors"),
        RTIME("rtime"),
        WDONE("wdone"),
        WMERGED("wmerged"),
        WSECTORS("wsectors"), // 10
        WTIME("wtime"),
        INPROGRESS_IO("inprogressIO"),
        IO_TIME("IOtime"),
        WEIGHTED_IO_TIME("weightedIOtime");
        private final String label;

        public String getLabel() {
            return label;
        }

        DiskKeys(String label) {
            this.label = label;
        }

        public static String[] getStatKeys() {
            return Stream.of(values()).map(DiskKeys::getLabel).toArray(String[]::new);
        }
    }

    private static SchemaFileParser.FieldTypes statTypes[] = {
        SchemaFileParser.FieldTypes.INT, // 1
        SchemaFileParser.FieldTypes.INT,
        SchemaFileParser.FieldTypes.STRING,
        SchemaFileParser.FieldTypes.ULONG,
        SchemaFileParser.FieldTypes.ULONG,
        SchemaFileParser.FieldTypes.ULONG,
        SchemaFileParser.FieldTypes.ULONG,
        SchemaFileParser.FieldTypes.ULONG,
        SchemaFileParser.FieldTypes.ULONG,
        SchemaFileParser.FieldTypes.ULONG, // 10
        SchemaFileParser.FieldTypes.ULONG,
        SchemaFileParser.FieldTypes.ULONG,
        SchemaFileParser.FieldTypes.ULONG,
        SchemaFileParser.FieldTypes.ULONG
    };

    private static final Logger LOG = LogManager.getLogger(DiskObserver.class);
    private static Set<String> diskList = new HashSet<>();

    static {
        Util.invokePrivileged(() -> listDisks());
    }

    @Override
    public Map<String, Map<String, Object>> observe() {
        Map<String, Map<String, Object>> diskKVMap = new HashMap<>();
        SchemaFileParser parser = new SchemaFileParser("/proc/diskstats", DiskKeys.getStatKeys(), statTypes);
        List<Map<String, Object>> sampleList = parser.parseMultiple();
        for (Map<String, Object> sample : sampleList) {
            String diskname = (String) (sample.get("name"));
            if (!diskList.contains(diskname)) {
                diskKVMap.put(diskname, sample);
            }
        }
        return diskKVMap;
    }

    @Override
    public Map<String, Object> observe(String diskName) {
        return observe().getOrDefault(diskName, Collections.emptyMap());
    }

    private static void listDisks() {
        try {
            File file = new File("/sys/block");
            File[] files = file.listFiles();
            if (files != null) {
                for (File dfile : files) {
                    if (dfile != null && !dfile.getCanonicalPath().contains("/virtual/")) {
                        diskList.add(dfile.getName());
                    }
                }
            }
        } catch (Exception e) {
            LOG.debug("Exception in calling listDisks with details: {}", () -> e.toString());
        }
    }
}

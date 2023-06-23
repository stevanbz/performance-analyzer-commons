/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.hwnet;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.opensearch.performanceanalyzer.commons.collectors.MountedPartitionMetrics;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.impl.MountedPartitionsObserver;
import org.opensearch.performanceanalyzer.commons.metrics_generator.MountedPartitionMetricsGenerator;
import org.opensearch.performanceanalyzer.commons.metrics_generator.linux.LinuxMountedPartitionMetricsGenerator;

public class MountedPartitions {
    private static final LinuxMountedPartitionMetricsGenerator
            linuxMountedPartitionMetricsGenerator;
    private static final Map<String, File> mountPointFileMap;

    private static final MountedPartitionsObserver mountedPartitionObserver;

    static {
        linuxMountedPartitionMetricsGenerator = new LinuxMountedPartitionMetricsGenerator();
        mountPointFileMap = new HashMap<>();
        mountedPartitionObserver = new MountedPartitionsObserver();
    }

    public static void addSample() {
        Map<String, String> mountPointDevicePartitionMap = mountedPartitionObserver.observe();
        for (Map.Entry<String, String> mountInfo : mountPointDevicePartitionMap.entrySet()) {
            String devicePartition = mountInfo.getValue();
            String mountPoint = mountInfo.getKey();

            long totalSpace =
                    mountPointFileMap.computeIfAbsent(mountPoint, File::new).getTotalSpace();
            long freeSpace = mountPointFileMap.get(mountPoint).getFreeSpace();
            long usableFreeSpace = mountPointFileMap.get(mountPoint).getUsableSpace();
            MountedPartitionMetrics metrics =
                    new MountedPartitionMetrics(
                            devicePartition, mountPoint, totalSpace, freeSpace, usableFreeSpace);

            linuxMountedPartitionMetricsGenerator.addSupplier(mountPoint, metrics);
        }
    }

    public static MountedPartitionMetricsGenerator getLinuxMountedPartitionMetricsGenerator() {
        return linuxMountedPartitionMetricsGenerator;
    }
}

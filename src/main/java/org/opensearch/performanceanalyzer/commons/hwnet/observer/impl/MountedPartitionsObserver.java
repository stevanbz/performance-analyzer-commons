/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.hwnet.observer.impl;


import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.NetObserver;
import org.opensearch.performanceanalyzer.commons.metrics_generator.linux.LinuxMountedPartitionMetricsGenerator;
import org.opensearch.performanceanalyzer.commons.os.SchemaFileParser;

public class MountedPartitionsObserver extends NetObserver<String> {

    private static final String CGROUP = "cgroup";
    private static final String PROC = "proc";
    private static final String SYSFS = "sysfs";
    private static final String DEV_PTS = "devpts";
    private static final String DEV_TMPFS = "devtmpfs";
    private static final String PATH_SYS = "/sys/";
    private static final String PATH_ETC = "/etc/";
    private static final String KEY_DEVICE_PARTITION = "devicePartition";
    private static final String KEY_MOUNT_POINT = "mountPoint";
    private static final String KEY_FILE_SYSTEM_TYPE = "fileSystemType";
    private static final String KEY_MOUNT_OPTIONS = "mountOptions";
    private static final String KEY_DUMP = "dump";
    private static final String KEY_PASS = "pass";
    private static final String NONE = "none";
    private static final LinuxMountedPartitionMetricsGenerator
            linuxMountedPartitionMetricsGenerator;
    private static final Map<String, File> mountPointFileMap;
    private static final Set<String> virtualSysPartitionSet;
    private static final Set<String> ignoredMountPoints;
    private static final String[] schemaKeys = {
        KEY_DEVICE_PARTITION,
        KEY_MOUNT_POINT,
        KEY_FILE_SYSTEM_TYPE,
        KEY_MOUNT_OPTIONS,
        KEY_DUMP,
        KEY_PASS
    };
    private static final SchemaFileParser.FieldTypes[] schemaKeyTypes = {
        SchemaFileParser.FieldTypes.STRING, // devicePartition
        SchemaFileParser.FieldTypes.STRING, // mountPoint
        SchemaFileParser.FieldTypes.STRING, // fileSystemType
        SchemaFileParser.FieldTypes.STRING, // mountOptions
        SchemaFileParser.FieldTypes.INT, // dump
        SchemaFileParser.FieldTypes.INT // pass
    };

    static {
        linuxMountedPartitionMetricsGenerator = new LinuxMountedPartitionMetricsGenerator();
        mountPointFileMap = new HashMap<>();
        virtualSysPartitionSet = ImmutableSet.of(CGROUP, PROC, SYSFS, DEV_PTS, DEV_TMPFS, NONE);
        ignoredMountPoints = ImmutableSet.of(PATH_SYS, PATH_ETC);
    }

    @Override
    public Map<String, String> observe() {
        Map<String, String> mountPointDevicePartitionMap = new HashMap<>();
        SchemaFileParser parser = new SchemaFileParser("/proc/mounts", schemaKeys, schemaKeyTypes);
        List<Map<String, Object>> parsedMaps =
                parser.parseMultiple().stream()
                        .filter(
                                map -> {
                                    String devicePartition = (String) map.get(KEY_DEVICE_PARTITION);
                                    String mountPoint = (String) map.get(KEY_MOUNT_POINT);

                                    return !(virtualSysPartitionSet.contains(devicePartition)
                                            || !devicePartition.startsWith("/dev/")
                                            || ignoredMountPoints.contains(mountPoint));
                                })
                        .collect(Collectors.toList());
        for (Map<String, Object> mountInfo : parsedMaps) {
            String devicePartition = (String) mountInfo.get(KEY_DEVICE_PARTITION);
            String mountPoint = (String) mountInfo.get(KEY_MOUNT_POINT);
            mountPointDevicePartitionMap.putIfAbsent(mountPoint, devicePartition);
        }
        return mountPointDevicePartitionMap;
    }
}

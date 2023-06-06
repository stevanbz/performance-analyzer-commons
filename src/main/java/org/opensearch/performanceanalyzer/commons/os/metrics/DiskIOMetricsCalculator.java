/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.os.metrics;


import java.util.Map;
import org.opensearch.performanceanalyzer.commons.os.observer.impl.IOObserver.StatKeys;

/** Calculates the disk io metrics for the threads considering the beginning and end measurements */
public final class DiskIOMetricsCalculator {
    public static IOMetrics calculateIOMetrics(
            long endMeasurementTime,
            long startMeasurementTime,
            Map<String, Long> endTimeResourceMetrics,
            Map<String, Long> startTimeResourceMetrics) {
        if (endMeasurementTime <= startMeasurementTime) {
            return null;
        }

        if (endTimeResourceMetrics != null && startTimeResourceMetrics != null) {
            double duration = 1.0e-3 * (endMeasurementTime - startMeasurementTime);
            double readBytes =
                    endTimeResourceMetrics.getOrDefault(StatKeys.READ_BYTES.getLabel(), 0L)
                            - startTimeResourceMetrics.getOrDefault(
                                    StatKeys.READ_BYTES.getLabel(), 0L);
            double writeBytes =
                    endTimeResourceMetrics.getOrDefault(StatKeys.WRITE_BYTES.getLabel(), 0L)
                            - startTimeResourceMetrics.getOrDefault(
                                    StatKeys.WRITE_BYTES.getLabel(), 0L);
            double readSyscalls =
                    endTimeResourceMetrics.getOrDefault(StatKeys.SYSCR.getLabel(), 0L)
                            - startTimeResourceMetrics.getOrDefault(StatKeys.SYSCR.getLabel(), 0L);
            double writeSyscalls =
                    endTimeResourceMetrics.getOrDefault(StatKeys.SYSCW.getLabel(), 0L)
                            - startTimeResourceMetrics.getOrDefault(StatKeys.SYSCW.getLabel(), 0L);
            double readPcBytes =
                    endTimeResourceMetrics.getOrDefault(StatKeys.RCHAR.getLabel(), 0L)
                            - startTimeResourceMetrics.getOrDefault(StatKeys.RCHAR.getLabel(), 0L)
                            - readBytes;
            double writePcBytes =
                    endTimeResourceMetrics.getOrDefault(StatKeys.WCHAR.getLabel(), 0L)
                            - startTimeResourceMetrics.getOrDefault(StatKeys.WCHAR.getLabel(), 0L)
                            - writeBytes;
            readBytes /= duration;
            readSyscalls /= duration;
            writeBytes /= duration;
            writeSyscalls /= duration;
            readPcBytes /= duration;
            writePcBytes /= duration;

            return new IOMetrics(
                    readBytes,
                    readSyscalls,
                    writeBytes,
                    writeSyscalls,
                    readBytes + writeBytes,
                    readSyscalls + writeSyscalls,
                    readPcBytes,
                    writePcBytes,
                    readPcBytes + writePcBytes);
        }
        return null;
    }
}

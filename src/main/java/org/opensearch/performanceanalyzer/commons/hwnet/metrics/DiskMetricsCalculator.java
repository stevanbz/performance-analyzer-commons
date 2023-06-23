/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.hwnet.metrics;


import java.util.HashMap;
import java.util.Map;
import org.opensearch.performanceanalyzer.commons.collectors.DiskMetrics;
import org.opensearch.performanceanalyzer.commons.hwnet.observer.impl.DiskObserver;

public class DiskMetricsCalculator {

    public static Map<String, DiskMetrics> calculateDiskMetrics(
            long endMeasurementTime,
            long startMeasurementTime,
            Map<String, Map<String, Object>> endTimeResourceMetrics,
            Map<String, Map<String, Object>> startTimeResourceMetrics) {
        Map<String, DiskMetrics> map = new HashMap<>();
        if (endMeasurementTime > startMeasurementTime) {
            for (Map.Entry<String, Map<String, Object>> entry : endTimeResourceMetrics.entrySet()) {
                String disk = entry.getKey();
                Map<String, Object> m = entry.getValue();
                Map<String, Object> mold = startTimeResourceMetrics.get(disk);
                if (mold != null) {
                    DiskMetrics dm = new DiskMetrics();
                    dm.name = (String) m.get(DiskObserver.DiskKeys.NAME.getLabel());
                    double rwdeltatime =
                            1.0
                                    * ((long) m.get(DiskObserver.DiskKeys.RTIME.getLabel())
                                            + (long) m.get(DiskObserver.DiskKeys.WTIME.getLabel())
                                            - (long)
                                                    mold.get(DiskObserver.DiskKeys.RTIME.getLabel())
                                            - (long)
                                                    mold.get(
                                                            DiskObserver.DiskKeys.WTIME
                                                                    .getLabel()));
                    double rwdeltaiops =
                            1.0
                                    * ((long) m.get(DiskObserver.DiskKeys.RDONE.getLabel())
                                            + (long) m.get(DiskObserver.DiskKeys.WDONE.getLabel())
                                            - (long)
                                                    mold.get(DiskObserver.DiskKeys.RDONE.getLabel())
                                            - (long)
                                                    mold.get(
                                                            DiskObserver.DiskKeys.WDONE
                                                                    .getLabel()));
                    double rwdeltasectors =
                            1.0
                                    * ((long) m.get(DiskObserver.DiskKeys.RSECTORS.getLabel())
                                            + (long)
                                                    m.get(DiskObserver.DiskKeys.WSECTORS.getLabel())
                                            - (long)
                                                    mold.get(
                                                            DiskObserver.DiskKeys.RSECTORS
                                                                    .getLabel())
                                            - (long)
                                                    mold.get(
                                                            DiskObserver.DiskKeys.WSECTORS
                                                                    .getLabel()));

                    dm.utilization = rwdeltatime / (endMeasurementTime - startMeasurementTime);
                    dm.await = (rwdeltaiops > 0) ? rwdeltatime / rwdeltaiops : 0;
                    dm.serviceRate =
                            (rwdeltatime > 0) ? rwdeltasectors * 512 * 1.0e-3 / rwdeltatime : 0;

                    map.put(disk, dm);
                }
            }
        }
        return map;
    }
}

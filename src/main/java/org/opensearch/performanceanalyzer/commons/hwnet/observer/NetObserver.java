/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.commons.hwnet.observer;


import java.util.Map;
import org.opensearch.performanceanalyzer.commons.observer.ResourceObserver;

public class NetObserver<T> implements ResourceObserver<T> {
    @Override
    public Map<String, T> observe() {
        throw new UnsupportedOperationException("Use exact network observer instead of general");
    }

    @Override
    public T observe(String threadId) {
        throw new UnsupportedOperationException(
                "Network observer is not supporting observing the resource by thread id");
    }
}

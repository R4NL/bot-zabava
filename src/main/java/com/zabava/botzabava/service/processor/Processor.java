
package com.zabava.botzabava.service.processor;

import com.zabava.botzabava.customaze.annotation.Retryable;
import com.zabava.botzabava.model.RequestData;

public interface Processor {
    @Retryable
    void process(RequestData requestData);

    boolean isPossibleToProcess(RequestData requestData);
}

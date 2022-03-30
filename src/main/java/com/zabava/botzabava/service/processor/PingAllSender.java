
package com.zabava.botzabava.service.processor;

import com.zabava.botzabava.model.RequestData;

//TODO think about implementation Update does not contain all users
public class PingAllSender implements Processor {
    public void process(RequestData requestData) {
    }

    public boolean isPossibleToProcess(RequestData requestData) {
        return false;
    }
}


package com.zabava.botzabava.service;

import com.zabava.botzabava.model.RequestData;
import com.zabava.botzabava.service.processor.Processor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class ProcessorFacade {
    private final Collection<Processor> processors;
    private ExecutorService executorService;

    public ProcessorFacade(final Collection<Processor> processors) {
        this.processors = processors;
    }

    @PostConstruct
    void init() {
        int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        this.executorService = new ThreadPoolExecutor(1, maximumPoolSize, 20L, TimeUnit.MINUTES, new SynchronousQueue<>());
    }

    public void process(RequestData requestData) {
        this.executorService.submit(() -> chooseProcessorAndApply(requestData));
    }

    private void chooseProcessorAndApply(RequestData requestData) {
        this.processors.stream()
                .filter((processor) -> processor.isPossibleToProcess(requestData))
                .findFirst()
                .ifPresent((processor) -> processor.process(requestData));
    }
}

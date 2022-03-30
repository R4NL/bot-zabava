
package com.zabava.botzabava.service.processor;

import com.zabava.botzabava.model.RequestData;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AudioSpamCleaner implements Processor {
    public static final int MAX_BOT_VOICE = 10;
    private Map<Long, Integer> userCount; //TODO guava Cache has ttl

    @PostConstruct
    void init() {
        this.userCount = new ConcurrentHashMap<>();
    }

    @SneakyThrows
    public void process(RequestData requestData) {
        Message message = requestData.update().getMessage();
        Long userSignature = message.getChat().getId();
        int counter = this.userCount.computeIfAbsent(userSignature, (s) -> 0);
        if (counter >= MAX_BOT_VOICE) {
            Integer messageId = message.getMessageId();
            Long chatId = message.getChatId();
            DeleteMessage deleteMessage = DeleteMessage.builder().messageId(messageId).chatId(chatId.toString()).build();
            requestData.bot().execute(deleteMessage);
        } else {
            this.userCount.put(userSignature, counter + 1);
        }
    }

    public boolean isPossibleToProcess(RequestData requestData) {
        return Optional.ofNullable(requestData)
                .map(RequestData::update)
                .map(Update::getMessage)
                .filter((message) -> message.getVoice() != null) //TODO mp3 also
                .filter((message) -> message.getViaBot() != null || message.getForwardFrom().getIsBot())
                .isPresent();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanCounter() {
        this.userCount.clear();
    }
}

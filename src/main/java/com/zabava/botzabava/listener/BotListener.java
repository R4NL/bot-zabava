
package com.zabava.botzabava.listener;

import com.zabava.botzabava.model.RequestData;
import com.zabava.botzabava.service.ProcessorFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class BotListener extends TelegramLongPollingBot {
    private final ProcessorFacade processor;

    public String getBotUsername() {
        return "VidgukBot_bot";
    }

    public String getBotToken() {
        return "1046321350:AAGsnll27wN3VSqU6vfxu3xUHsWNmZC83sw";
    }

    public void onUpdateReceived(Update update) {
        this.processor.process(RequestData.of(this, update));
    }
}

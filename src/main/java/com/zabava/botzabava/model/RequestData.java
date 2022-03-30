
package com.zabava.botzabava.model;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public record RequestData(TelegramLongPollingBot bot, Update update) {
    public static RequestData of(TelegramLongPollingBot bot, Update update) {
        return new RequestData(bot, update);
    }

}

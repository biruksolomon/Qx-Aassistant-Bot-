package com.Qx.assistant.config;

import com.Qx.assistant.service.QxBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class BotConfig extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String username;

    @Value("${telegram.bot.token}")
    private String token;

    @Autowired
    private QxBotService botService;

    @Override
    public void onUpdateReceived(Update update) {
        botService.handleUpdate(update);
    }

    @Override
    public String getBotUsername() { return username; }

    @Override
    public String getBotToken() { return token; }
}

package com.Qx.assistant.service;

import com.Qx.assistant.config.BotConfig;
import com.Qx.assistant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Service
public class QxBotService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ReferralService referralService;

    @Autowired
    private RewardService rewardService;

    @Autowired
    private BotConfig bot; // your TelegramLongPollingBot component


    public void handleUpdate(Update update) {

        if (!update.hasMessage()) return;

        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String username = msg.getFrom().getUserName();

        // Detect /start with referral
        if (msg.hasText() && msg.getText().startsWith("/start")) {

            String[] parts = msg.getText().split(" ");

            if (parts.length > 1) {
                String inviterCode = parts[1];
                referralService.processReferral(chatId, username, inviterCode);
            } else {
                referralService.registerUser(chatId, username);
            }

            send(chatId, "Welcome to QX Assistant Bot!\n\nHere is your referral link:\n" +
                    "https://t.me/qx_ethiopia_bot?start=" + userRepo.findById(chatId).get().getReferralCode());
        }
    }

    private void send(Long chatId, String text) {
        try {
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId.toString());
            sm.setText(text);
            bot.execute(sm); // <- execute the send via the registered bot instance
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

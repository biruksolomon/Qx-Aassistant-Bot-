package com.Qx.assistant.service;

import com.Qx.assistant.config.BotConfig;
import com.Qx.assistant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class QxBotService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ReferralService referralService;

    @Autowired
    private RewardService rewardService;

    @Autowired
    private MembershipVerificationService membershipVerificationService;

    @Autowired
    private BotConfig bot;

    public void handleUpdate(Update update) {

        if (!update.hasMessage()) return;

        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String username = msg.getFrom().getUserName();
        Long userId = msg.getFrom().getId();

        if (!membershipVerificationService.isUserMember(userId)) {
            send(chatId, membershipVerificationService.getNotMemberMessage(), createJoinedKeyboard());
            return;
        }

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
                    "https://t.me/qx_ethiopia_bot?start=" + userRepo.findById(chatId).get().getReferralCode(), null);
        }

        if (msg.hasText() && msg.getText().equals("I Joined")) {
            if (membershipVerificationService.isUserMember(userId)) {
                send(chatId, "✅ Great! You've successfully joined our community. Now you can use the bot.\n\nClick /start to begin!", null);
            } else {
                send(chatId, "❌ You haven't joined both the channel and group yet.\n\n" +
                        membershipVerificationService.getNotMemberMessage(), createJoinedKeyboard());
            }
        }
    }

    private ReplyKeyboardMarkup createJoinedKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("I Joined"));
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private void send(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        try {
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId.toString());
            sm.setText(text);
            if (keyboard != null) {
                sm.setReplyMarkup(keyboard);
            }
            bot.execute(sm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send(Long chatId, String text) {
        send(chatId, text, null);
    }
}

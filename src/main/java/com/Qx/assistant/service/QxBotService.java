package com.Qx.assistant.service;

import com.Qx.assistant.config.BotConfig;
import com.Qx.assistant.model.User;
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
    private RewardDisplayService rewardDisplayService; // Added reward display service

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

        if (msg.hasText() && msg.getText().startsWith("/start")) {

            String[] parts = msg.getText().split(" ");

            if (parts.length > 1) {
                String inviterCode = parts[1];
                referralService.processReferral(chatId, username, inviterCode);
            } else {
                referralService.registerUser(chatId, username);
            }

            User user = userRepo.findById(chatId).get();
            String welcomeMsg = "Welcome to QX Assistant Bot!\n\n" +
                    "Here is your referral link:\n" +
                    "https://t.me/qx_ethiopia_bot?start=" + user.getReferralCode() + "\n\n" +
                    "Click /rewards to see your rewards!";
            send(chatId, welcomeMsg, createMainKeyboard());
        }

        if (msg.hasText() && msg.getText().equals("/rewards")) {
            User user = userRepo.findById(chatId).orElse(null);
            if (user != null) {
                send(chatId, rewardDisplayService.getRewardsDescription(user), createMainKeyboard());
            }
        }

        if (msg.hasText() && msg.getText().equals("📊 My Referrals")) {
            User user = userRepo.findById(chatId).orElse(null);
            if (user != null) {
                String refMsg = "👤 *Your Referral Stats*\n\n" +
                        "Referral Code: `" + user.getReferralCode() + "`\n" +
                        "Total Invites: *" + user.getInviteCount() + "*\n\n" +
                        "Share your code to earn rewards!";
                send(chatId, refMsg, createMainKeyboard());
            }
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

    private ReplyKeyboardMarkup createMainKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("🎁 View Rewards"));
        row1.add(new KeyboardButton("📊 My Referrals"));
        keyboard.add(row1);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private void send(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        try {
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId.toString());
            sm.setText(text);
            sm.setParseMode("Markdown");
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

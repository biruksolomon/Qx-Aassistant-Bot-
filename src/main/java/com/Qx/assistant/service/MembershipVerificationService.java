package com.Qx.assistant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import com.Qx.assistant.config.BotConfig;

@Service
public class MembershipVerificationService {

    @Autowired
    private BotConfig bot;

    // Channel and group identifiers
    private static final String CHANNEL_USERNAME = "@qx_ethiopia";
    private static final String GROUP_USERNAME = "@qx_ethiopia_group";

    /**
     * Checks if user is a member of both required channel and group
     */
    public boolean isUserMember(Long userId) {
        return isMemberOfChannel(userId) && isMemberOfGroup(userId);
    }

    /**
     * Checks if user is a member of the channel
     */
    private boolean isMemberOfChannel(Long userId) {
        return checkChatMembership(userId, CHANNEL_USERNAME);
    }

    /**
     * Checks if user is a member of the group
     */
    private boolean isMemberOfGroup(Long userId) {
        return checkChatMembership(userId, GROUP_USERNAME);
    }

    /**
     * Helper method to check membership in a specific chat
     */
    private boolean checkChatMembership(Long userId, String chatId) {
        try {
            GetChatMember getChatMember = new GetChatMember();
            getChatMember.setChatId(chatId);
            getChatMember.setUserId(userId);

            ChatMember chatMember = bot.execute(getChatMember);
            String status = chatMember.getStatus();

            // User is a member if they have a status other than "left" or "kicked"
            return !status.equals("left") && !status.equals("kicked");
        } catch (Exception e) {
            // If we can't verify membership, assume not a member
            System.out.println("[v0] Error checking membership for user " + userId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns the message to send when user is not a member
     */
    public String getNotMemberMessage() {
        return "❌ You must join the channel before using the bot.\n\n" +
                "To start using QX Assistant, please join our community:\n\n" +
                "📢 Official Channel: @qx_ethiopia\n" +
                "💬 Group Chat: @qx_ethiopia_group\n\n" +
                "After joining, click \"I Joined\"";
    }
}

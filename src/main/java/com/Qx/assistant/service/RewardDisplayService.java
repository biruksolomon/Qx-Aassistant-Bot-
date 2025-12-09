package com.Qx.assistant.service;

import com.Qx.assistant.model.User;
import org.springframework.stereotype.Service;

@Service
public class RewardDisplayService {

    public String getRewardsDescription(User user) {
        int invites = user.getInviteCount();

        StringBuilder sb = new StringBuilder();
        sb.append("🎁 *QX REWARDS SYSTEM* 🎁\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        sb.append("Your Current Invites: *").append(invites).append("*\n\n");

        // Tier 1: 10 Invites
        sb.append(getRewardTier(invites, 10,
                "🏅 *TIER 1: 10+ INVITES*",
                "✅ 10 Reward Points",
                "Upcoming QX Reward",
                user.isReward10()));

        // Tier 2: 50 Invites
        sb.append(getRewardTier(invites, 50,
                "🥈 *TIER 2: 50+ INVITES*",
                "✅ 3 Products with Free Delivery",
                "From QX Marketplace",
                user.isReward50()));

        // Tier 3: 100 Invites
        sb.append(getRewardTier(invites, 100,
                "🥇 *TIER 3: 100+ INVITES*",
                "✅ 10% OFF + Free Delivery",
                "From QX Marketplace",
                user.isReward100()));

        // Tier 4: 500 Invites
        sb.append(getRewardTier(invites, 500,
                "👑 *TIER 4: 500+ INVITES*",
                "✅ 300 Birr Cash Voucher",
                "Special VIP Reward",
                user.isReward500()));

        sb.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("💡 Keep inviting to earn more rewards!");

        return sb.toString();
    }

    private String getRewardTier(int currentInvites, int requiredInvites,
                                 String tierName, String reward, String description, boolean isUnlocked) {
        StringBuilder sb = new StringBuilder();

        if (currentInvites >= requiredInvites) {
            sb.append("✅ ").append(tierName).append("\n");
        } else {
            sb.append("🔒 ").append(tierName).append("\n");
        }

        sb.append("   Reward: ").append(reward).append("\n");
        sb.append("   ").append(description).append("\n");

        if (isUnlocked) {
            sb.append("   Status: *CLAIMED* ✨\n");
        } else if (currentInvites >= requiredInvites) {
            sb.append("   Status: *READY TO CLAIM* 🎯\n");
        } else {
            sb.append("   Progress: ").append(currentInvites).append("/").append(requiredInvites).append("\n");
        }

        sb.append("\n");
        return sb.toString();
    }
}

package com.Qx.assistant.service;

import com.Qx.assistant.model.Reward;
import com.Qx.assistant.model.User;
import com.Qx.assistant.repository.RewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RewardService {

    @Autowired
    private RewardRepository rewardRepo;

    public void checkRewards(User inviter) {
        int count = inviter.getInviteCount();

        if (count >= 10 && !inviter.isReward10())
            grantReward(inviter, "10", "QX10-FIRST");

        if (count >= 50 && !inviter.isReward50())
            grantReward(inviter, "50", "QX50-BONUS");

        if (count >= 100 && !inviter.isReward100())
            grantReward(inviter, "100", "QX100-MEGA");

        if (count >= 500 && !inviter.isReward500())
            grantReward(inviter, "500", "QX500-VIP");
    }

    private void grantReward(User user, String type, String promo) {

        Reward reward = new Reward();
        reward.setUserChatId(user.getChatId());
        reward.setRewardType(type);
        reward.setPromoCode(promo);

        rewardRepo.save(reward);

        System.out.println("User " + user.getChatId() + " Won Promo: " + promo);
    }
}

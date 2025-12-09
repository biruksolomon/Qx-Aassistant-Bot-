package com.Qx.assistant.service;

import com.Qx.assistant.model.User;
import com.Qx.assistant.repository.ReferralRepository;
import com.Qx.assistant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReferralService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ReferralRepository referralRepo;

    @Autowired
    private RewardService rewardService;

    public void registerUser(Long chatId, String username) {
        if (userRepo.existsById((chatId))) return;

        User user = new User();
        user.setChatId(chatId);
        user.setUsername(username);
        user.setReferralCode("QX" + chatId);
        user.setInviteCount(0);

        userRepo.save(user);
    }

    public void processReferral(Long chatId, String username, String inviterCode) {

        registerUser(chatId, username);

        User invitedUser = userRepo.findById((chatId)).get();
        if (invitedUser.getInvitedBy() != null) return;  // Already referred

        User inviter = userRepo.findByReferralCode(inviterCode);
        if (inviter == null) return;

        invitedUser.setInvitedBy(inviterCode);
        userRepo.save(invitedUser);

        inviter.setInviteCount(inviter.getInviteCount() + 1);
        userRepo.save(inviter);

        rewardService.checkRewards(inviter);
    }
}

package com.Qx.assistant.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Reward {

    @Id
    @GeneratedValue
    private Long id;

    private Long userChatId;
    private String rewardType;   // “10”, “50”, “100”, “500”
    private String promoCode;
}

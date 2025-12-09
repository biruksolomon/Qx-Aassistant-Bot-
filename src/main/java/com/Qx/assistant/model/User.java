package com.Qx.assistant.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    private Long chatId;

    private String username;
    private String referralCode;
    private String invitedBy;
    private int inviteCount;

    private boolean reward10;
    private boolean reward50;
    private boolean reward100;
    private boolean reward500;
}

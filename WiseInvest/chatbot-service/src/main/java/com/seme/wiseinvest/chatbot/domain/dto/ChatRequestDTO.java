package com.seme.wiseinvest.chatbot.domain.dto;

import lombok.Data;
@Data
public class ChatRequestDTO {
    private String user_id;   // 用户ID
    private String message;   // 用户消息内容
}
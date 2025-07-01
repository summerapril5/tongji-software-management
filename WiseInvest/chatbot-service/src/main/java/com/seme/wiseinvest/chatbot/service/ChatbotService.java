package com.seme.wiseinvest.chatbot.service;

import com.seme.wiseinvest.chatbot.domain.dto.ChatRequestDTO;
import com.seme.wiseinvest.chatbot.domain.vo.ChatReplyVO;

public interface ChatbotService {
    ChatReplyVO getReply(ChatRequestDTO requestDTO);
}

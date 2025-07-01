package com.seme.wiseinvest.chatbot.controller;

import com.seme.wiseinvest.chatbot.domain.dto.ChatRequestDTO;
import com.seme.wiseinvest.chatbot.domain.vo.ChatReplyVO;
import com.seme.wiseinvest.chatbot.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    private final ChatbotService chatbotService;

    @PostMapping("/chat")
    public ChatReplyVO chat(@RequestBody ChatRequestDTO requestDTO) {
        System.out.println("requestDTO: " + requestDTO);
        return chatbotService.getReply(requestDTO);
    }
    
}
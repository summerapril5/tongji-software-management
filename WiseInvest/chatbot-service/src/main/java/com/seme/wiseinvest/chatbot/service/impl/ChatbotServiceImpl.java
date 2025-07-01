package com.seme.wiseinvest.chatbot.service.impl;

import com.seme.wiseinvest.chatbot.domain.dto.ChatRequestDTO;
import com.seme.wiseinvest.chatbot.domain.vo.ChatReplyVO;
import com.seme.wiseinvest.chatbot.service.ChatbotService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    // 智能API的URL，通过配置文件注入
    @Value("${chatbot.api.url}")
    private String chatbotApiUrl;

    // Spring的HTTP客户端
    private final RestTemplate restTemplate = new RestTemplate();

    // 调用外部API获取智能回复
    @Override
    public ChatReplyVO getReply(ChatRequestDTO requestDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChatRequestDTO> entity = new HttpEntity<>(requestDTO, headers);
        ResponseEntity<ChatReplyVO> response = restTemplate.postForEntity(chatbotApiUrl, entity, ChatReplyVO.class);
        return response.getBody();
    }
}

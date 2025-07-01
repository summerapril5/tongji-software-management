package com.seme.wiseinvest.chatbot.service.impl;

import com.seme.wiseinvest.chatbot.domain.dto.ChatRequestDTO;
import com.seme.wiseinvest.chatbot.domain.vo.ChatReplyVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest(properties = {
        "chatbot.api.url=http://mock-chat-api.com/reply"
})
public class ChatbotServiceImplTest {

    @Value("${chatbot.api.url}")
    private String chatbotApiUrl;

    private ChatbotServiceImpl chatbotService;

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        chatbotService = new ChatbotServiceImpl();
        chatbotService.chatbotApiUrl = chatbotApiUrl;
        chatbotService.getClass().getDeclaredFields();
        // 反射注入 RestTemplate（测试用）
        try {
            var field = chatbotService.getClass().getDeclaredField("restTemplate");
            field.setAccessible(true);
            field.set(chatbotService, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getReply_shouldReturnExpectedReply_whenApiResponds() {
        ChatRequestDTO requestDTO = new ChatRequestDTO();
        requestDTO.setUserId(10001L);
        requestDTO.setMessage("What are safe investments?");

        ChatReplyVO mockReply = new ChatReplyVO();
        mockReply.setReply("Consider diversified index funds.");

        mockServer.expect(requestTo(chatbotApiUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {
                          "reply": "Consider diversified index funds."
                        }
                        """, MediaType.APPLICATION_JSON));

        ChatReplyVO actual = chatbotService.getReply(requestDTO);
        assertEquals("Consider diversified index funds.", actual.getReply());
    }
}

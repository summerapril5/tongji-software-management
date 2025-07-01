package com.seme.wiseinvest.chatbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seme.wiseinvest.chatbot.domain.dto.ChatRequestDTO;
import com.seme.wiseinvest.chatbot.domain.vo.ChatReplyVO;
import com.seme.wiseinvest.chatbot.service.ChatbotService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatbotController.class)
public class ChatbotControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatbotService chatbotService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void chat_shouldReturnReply_whenValidRequest() throws Exception {
        ChatRequestDTO requestDTO = new ChatRequestDTO();
        requestDTO.setUserId(10001L);
        requestDTO.setMessage("Hello, what can I invest in?");

        ChatReplyVO replyVO = new ChatReplyVO();
        replyVO.setReply("You can consider low-risk bond funds.");

        when(chatbotService.getReply(requestDTO)).thenReturn(replyVO);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("You can consider low-risk bond funds."));
    }
}

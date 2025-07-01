package com.seme.wiseinvest.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seme.wiseinvest.account.domain.dto.LoginDTO;
import com.seme.wiseinvest.account.service.LoginService;
import com.seme.wiseinvest.common.utils.JwtUtil; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

@WebMvcTest(LoginController.class) // 只测试LoginController，并mock LoginService
public class LoginControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginService loginService;

    @Test
    void login_success_customer() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhoneNumber("13900139000");
        loginDTO.setPassword("password123");
        loginDTO.setUserType(1); // Customer

        // 模拟LoginService返回一个长字符串（例如JWT Token）
        String mockToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImV4cCI6MTY3OTg4ODAwMH0.verylongmocktokenstring";
        when(loginService.checkPassword(any(LoginDTO.class))).thenReturn(mockToken);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.message", is("操作成功")))
                .andExpect(jsonPath("$.data", is(mockToken)));
    }

    @Test
    void login_failure_wrongPassword() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhoneNumber("13900139000");
        loginDTO.setPassword("wrongpassword");
        loginDTO.setUserType(1);

        // 模拟LoginService返回一个短错误信息
        String errorMessage = "密码错误";
        when(loginService.checkPassword(any(LoginDTO.class))).thenReturn(errorMessage);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(1))) // 假设错误码为1
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void validPhoneNumber_success_exists() throws Exception {
        String phoneNumber = "13900139001";
        when(loginService.isVaildPhoneNumber(phoneNumber)).thenReturn(true);

        mockMvc.perform(get("/login/verification")
                .param("phoneNumber", phoneNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.message", is("操作成功")));
    }

    @Test
    void validPhoneNumber_failure_notExists() throws Exception {
        String phoneNumber = "13900139002";
        when(loginService.isVaildPhoneNumber(phoneNumber)).thenReturn(false);

        mockMvc.perform(get("/login/verification")
                .param("phoneNumber", phoneNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(1)))
                .andExpect(jsonPath("$.message", is("手机号没有匹配的账号")));
    }

    @Test
    void validAdminPhoneNumber_success_exists() throws Exception {
        String phoneNumber = "13800138001";
        when(loginService.isVaildAdminPhoneNumber(phoneNumber)).thenReturn(true);

        mockMvc.perform(get("/login/admin_verification")
                .param("phoneNumber", phoneNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.message", is("操作成功")));
    }

    @Test
    void validAdminPhoneNumber_failure_notExists() throws Exception {
        String phoneNumber = "13800138002";
        when(loginService.isVaildAdminPhoneNumber(phoneNumber)).thenReturn(false);

        mockMvc.perform(get("/login/admin_verification")
                .param("phoneNumber", phoneNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(1)))
                .andExpect(jsonPath("$.message", is("手机号没有匹配的账号")));
    }

    @Test
    void changePassword_success() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhoneNumber("13900139000");
        loginDTO.setPassword("newPassword123");
        loginDTO.setUserType(1);

        when(loginService.changePassword(any(LoginDTO.class))).thenReturn(true);

        mockMvc.perform(patch("/login/change_password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.message", is("操作成功")));
    }

    @Test
    void changePassword_failure() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhoneNumber("13900139000");
        loginDTO.setPassword("newPassword123");
        loginDTO.setUserType(1);

        when(loginService.changePassword(any(LoginDTO.class))).thenReturn(false);

        mockMvc.perform(patch("/login/change_password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(1)))
                .andExpect(jsonPath("$.message", is("修改密码失败")));
    }
}
package com.seme.wiseinvest.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.seme.wiseinvest.account.domain.Admin;
import com.seme.wiseinvest.account.domain.Customer;
import com.seme.wiseinvest.account.domain.dto.LoginDTO;
import com.seme.wiseinvest.account.mapper.AdminMapper;
import com.seme.wiseinvest.account.mapper.CustomerMapper;
import com.seme.wiseinvest.common.utils.JwtUtil;
import com.seme.wiseinvest.common.utils.Md5Util;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private AdminMapper adminMapper;

    @InjectMocks
    private LoginServiceImpl loginService;

    private MockedStatic<Md5Util> mockedMd5Util;
    private MockedStatic<JwtUtil> mockedJwtUtil;

    @BeforeEach
    void setUp() {
        mockedMd5Util = Mockito.mockStatic(Md5Util.class);
        mockedJwtUtil = Mockito.mockStatic(JwtUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedMd5Util.close();
        mockedJwtUtil.close();
    }

    @Test
    void checkPassword_customer_success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhoneNumber("13900000000");
        loginDTO.setPassword("password123");
        loginDTO.setUserType(1);

        Customer customer = new Customer();
        customer.setFundAccount(1L);
        customer.setName("Test Customer");
        customer.setRiskLevel(1);
        customer.setPassword("hashedPassword"); // Assume this is the stored hashed password

        when(customerMapper.selectOne(any(QueryWrapper.class))).thenReturn(customer);
        mockedMd5Util.when(() -> Md5Util.checkPassword("password123", "hashedPassword")).thenReturn(true);
        mockedJwtUtil.when(() -> JwtUtil.genToken(any(Map.class))).thenReturn("mocked_jwt_token");

        String result = loginService.checkPassword(loginDTO);

        assertEquals("mocked_jwt_token", result);
        verify(customerMapper, times(1)).selectOne(any(QueryWrapper.class));
    }

    @Test
    void checkPassword_customer_notFound() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhoneNumber("13900000000");
        loginDTO.setPassword("password123");
        loginDTO.setUserType(1);

        when(customerMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        String result = loginService.checkPassword(loginDTO);

        assertEquals("该手机号暂未开户", result);
    }

    @Test
    void checkPassword_customer_wrongPassword() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhoneNumber("13900000000");
        loginDTO.setPassword("wrongPassword");
        loginDTO.setUserType(1);

        Customer customer = new Customer();
        customer.setPassword("hashedPassword");

        when(customerMapper.selectOne(any(QueryWrapper.class))).thenReturn(customer);
        mockedMd5Util.when(() -> Md5Util.checkPassword("wrongPassword", "hashedPassword")).thenReturn(false);

        String result = loginService.checkPassword(loginDTO);

        assertEquals("密码错误", result);
    }

    @Test
    void checkPassword_admin_success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhoneNumber("13800000000");
        loginDTO.setPassword("adminPass");
        loginDTO.setUserType(2);

        Admin admin = new Admin();
        admin.setAdminAccount(100L);
        admin.setPassword("hashedAdminPass");

        when(adminMapper.selectOne(any(QueryWrapper.class))).thenReturn(admin);
        mockedMd5Util.when(() -> Md5Util.checkPassword("adminPass", "hashedAdminPass")).thenReturn(true);
        mockedJwtUtil.when(() -> JwtUtil.genToken(any(Map.class))).thenReturn("mocked_admin_jwt_token");

        String result = loginService.checkPassword(loginDTO);

        assertEquals("mocked_admin_jwt_token", result);
    }

    @Test
    void checkPassword_admin_notFound() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhoneNumber("13800000000");
        loginDTO.setUserType(2);

        when(adminMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        String result = loginService.checkPassword(loginDTO);
        assertEquals("该手机号暂未注册管理员", result);
    }

    @Test
    void checkPassword_invalidUserType() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUserType(3); // Invalid type

        String result = loginService.checkPassword(loginDTO);
        assertEquals("非法用户类型", result);
    }

    @Test
    void isValidPhoneNumber_true() {
        when(customerMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);
        assertTrue(loginService.isVaildPhoneNumber("13900000000"));
    }

    @Test
    void isValidPhoneNumber_false() {
        when(customerMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        assertFalse(loginService.isVaildPhoneNumber("13900000000"));
    }

    @Test
    void isValidAdminPhoneNumber_true() {
        when(adminMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);
        assertTrue(loginService.isVaildAdminPhoneNumber("13800000000"));
    }

    @Test
    void isValidAdminPhoneNumber_false() {
        when(adminMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        assertFalse(loginService.isVaildAdminPhoneNumber("13800000000"));
    }

    @Test
    void changePassword_customer_success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhoneNumber("13900000000");
        loginDTO.setPassword("newPassword");
        loginDTO.setUserType(1);

        mockedMd5Util.when(() -> Md5Util.getMD5String("newPassword")).thenReturn("hashedNewPassword");
        when(customerMapper.update(isNull(), any(UpdateWrapper.class))).thenReturn(1);

        assertTrue(loginService.changePassword(loginDTO));
        verify(customerMapper, times(1)).update(isNull(), argThat(uw -> uw.getSqlSet().contains("'hashedNewPassword'")));
    }

    @Test
    void changePassword_admin_success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhoneNumber("13800000000");
        loginDTO.setPassword("newAdminPassword");
        loginDTO.setUserType(2);

        mockedMd5Util.when(() -> Md5Util.getMD5String("newAdminPassword")).thenReturn("hashedNewAdminPassword");
        when(adminMapper.update(isNull(), any(UpdateWrapper.class))).thenReturn(1);

        assertTrue(loginService.changePassword(loginDTO));
        verify(adminMapper, times(1)).update(isNull(), argThat(uw -> uw.getSqlSet().contains("'hashedNewAdminPassword'")));
    }

    @Test
    void changePassword_invalidUserType() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUserType(3);
        assertFalse(loginService.changePassword(loginDTO));
    }
}
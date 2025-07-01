package com.seme.wiseinvest.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seme.wiseinvest.account.domain.Admin;
import com.seme.wiseinvest.account.mapper.AdminMapper;
import com.seme.wiseinvest.common.utils.Md5Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private AdminMapper adminMapper; // Mock the mapper

    @InjectMocks
    private AdminServiceImpl adminService; // Inject mocks into the service

    private Admin admin;

    @BeforeEach
    void setUp() {
        admin = new Admin();
        admin.setAdminAccount(1L);
        admin.setPhoneNumber("13800138000");
        admin.setPassword("password123");
    }

    @Test
    void createAdmin_success_newUser() {
        try (MockedStatic<Md5Util> mockedMd5Util = Mockito.mockStatic(Md5Util.class)) {
            mockedMd5Util.when(() -> Md5Util.getMD5String(anyString())).thenReturn("hashedPassword");

            when(adminMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L); // No existing admin with this phone
            when(adminMapper.insert(any(Admin.class))).thenReturn(1); // Simulate successful insert

            boolean result = adminService.createAdmin(admin);

            assertTrue(result);
            assertEquals("hashedPassword", admin.getPassword()); // Verify password was hashed
            verify(adminMapper, times(1)).selectCount(any(QueryWrapper.class));
            verify(adminMapper, times(1)).insert(admin);
        }
    }

    @Test
    void createAdmin_failure_userExists() {
        when(adminMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L); // Admin with this phone already exists

        boolean result = adminService.createAdmin(admin);

        assertFalse(result);
        verify(adminMapper, times(1)).selectCount(any(QueryWrapper.class));
        verify(adminMapper, never()).insert(any(Admin.class)); // Ensure insert was not called
    }
}
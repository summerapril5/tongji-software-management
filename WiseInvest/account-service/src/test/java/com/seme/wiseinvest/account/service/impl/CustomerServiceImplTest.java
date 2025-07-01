package com.seme.wiseinvest.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seme.wiseinvest.account.domain.Customer;
import com.seme.wiseinvest.account.domain.dto.CustomerDTO;
import com.seme.wiseinvest.account.domain.dto.UpdateInfoDTO;
import com.seme.wiseinvest.account.domain.vo.CustomerVO;
import com.seme.wiseinvest.account.mapper.CustomerMapper;
import com.seme.wiseinvest.common.utils.Md5Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customerDTO = new CustomerDTO();
        customerDTO.setPhoneNumber("13800138000");
        customerDTO.setIdNumber("123456789012345678");
        customerDTO.setPassword("password123");
        customerDTO.setName("张三");
        customerDTO.setRiskLevel(3);
    }

    @Test
    void createAccount_success() {
        // Mock Md5Util.getMD5String to avoid actual MD5 calculation in unit test
        try (MockedStatic<Md5Util> mockedMd5Util = Mockito.mockStatic(Md5Util.class)) {
            mockedMd5Util.when(() -> Md5Util.getMD5String(anyString())).thenReturn("hashedPassword");

            // Mock customerMapper.selectCount to simulate no existing account
            when(customerMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);

            when(customerMapper.insert(any(Customer.class))).thenAnswer(invocation -> {
                Customer c = invocation.getArgument(0);
                return 1; // Simulate 1 row affected
            });

            Long fundAccount = customerService.createAccount(customerDTO);

            assertNotNull(fundAccount);
            // we just check it's not 0L, which indicates success in the method's logic.
            assertNotEquals(0L, fundAccount, "Fund account should be created.");

            // Verify that Md5Util.getMD5String was called
            mockedMd5Util.verify(() -> Md5Util.getMD5String("password123"));
            // Verify that customerMapper.selectCount was called once
            verify(customerMapper, times(1)).selectCount(any(QueryWrapper.class));
            // Verify that customerMapper.insert (via save) was called once
        }
    }

    @Test
    void createAccount_failure_accountExists() {
        // Mock customerMapper.selectCount to simulate an existing account
        when(customerMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        Long fundAccount = customerService.createAccount(customerDTO);

        assertEquals(0L, fundAccount, "Fund account should not be created if account already exists.");

        // Verify that customerMapper.insert (via save) was NOT called
        verify(customerMapper, never()).insert(any(Customer.class));
    }

    @Test
    void updateRiskLevel_success() {
        Long fundAccount = 12345L;
        int newRiskLevel = 3;

        when(customerMapper.update(isNull(), any(UpdateWrapper.class))).thenReturn(1); // Simulate 1 row updated

        boolean result = customerService.updateRiskLevel(fundAccount, newRiskLevel);

        assertTrue(result);
        verify(customerMapper, times(1)).update(isNull(), any(UpdateWrapper.class));
    }

    @Test
    void updateRiskLevel_failure_noAccountFound() {
        Long fundAccount = 12345L;
        int newRiskLevel = 3;

        when(customerMapper.update(isNull(), any(UpdateWrapper.class))).thenReturn(0); // Simulate 0 rows updated

        boolean result = customerService.updateRiskLevel(fundAccount, newRiskLevel);

        assertFalse(result);
        verify(customerMapper, times(1)).update(isNull(), any(UpdateWrapper.class));
    }

    @Test
    void updateInfo_success() {
        UpdateInfoDTO updateInfoDTO = new UpdateInfoDTO();
        updateInfoDTO.setFundAccount(1L);
        updateInfoDTO.setPhoneNumber("13912345678");
        updateInfoDTO.setRiskLevel(2);

        // Mock that the phone number does not exist for another user
        QueryWrapper<Customer> phoneQuery = new QueryWrapper<>();
        phoneQuery.eq("phone_number", updateInfoDTO.getPhoneNumber());
        when(customerMapper.selectCount(argThat(wrapper -> wrapper.getSqlSelect().contains("phone_number")))).thenReturn(0L);

        // Mock successful update
        when(customerMapper.update(isNull(), any(UpdateWrapper.class))).thenReturn(1);

        boolean result = customerService.updateInfo(updateInfoDTO);

        assertTrue(result);
        verify(customerMapper, times(1)).selectCount(argThat(wrapper -> wrapper.getSqlSelect().contains("phone_number")));
        verify(customerMapper, times(1)).update(isNull(), any(UpdateWrapper.class));
    }

    @Test
    void updateInfo_failure_phoneNumberExists() {
        UpdateInfoDTO updateInfoDTO = new UpdateInfoDTO();
        updateInfoDTO.setFundAccount(1L);
        updateInfoDTO.setPhoneNumber("13912345678");
        updateInfoDTO.setRiskLevel(2);

        // Mock that the phone number already exists for another user
        when(customerMapper.selectCount(argThat(wrapper -> wrapper.getSqlSelect().contains("phone_number")))).thenReturn(1L);

        boolean result = customerService.updateInfo(updateInfoDTO);

        assertFalse(result);
        verify(customerMapper, times(1)).selectCount(argThat(wrapper -> wrapper.getSqlSelect().contains("phone_number")));
        verify(customerMapper, never()).update(any(), any(UpdateWrapper.class));
    }

    @Test
    void getCustomers_success_withKey() {
        int pageNum = 1;
        int pageSize = 10;
        String key = "John";

        Page<Customer> page = new Page<>(pageNum, pageSize);
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setFundAccount(1L);
        page.setRecords(Collections.singletonList(customer));
        page.setTotal(1);

        when(customerMapper.selectPage(any(IPage.class), any(QueryWrapper.class))).thenReturn(page);

        List<CustomerVO> result = customerService.getCustomers(pageNum, pageSize, key);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(customerMapper, times(1)).selectPage(any(IPage.class), argThat(qw -> qw.getSqlSelect() != null && qw.getSqlSelect().contains("name LIKE")));
    }

    @Test
    void getCustomers_success_withoutKey() {
        int pageNum = 1;
        int pageSize = 10;
        String key = ""; // Empty key

        Page<Customer> page = new Page<>(pageNum, pageSize);
        Customer customer = new Customer();
        customer.setName("Jane Doe");
        customer.setFundAccount(2L);
        page.setRecords(Collections.singletonList(customer));
        page.setTotal(1);

        when(customerMapper.selectPage(any(IPage.class), isNull())).thenReturn(page); // Expecting null QueryWrapper

        List<CustomerVO> result = customerService.getCustomers(pageNum, pageSize, key);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jane Doe", result.get(0).getName());
        verify(customerMapper, times(1)).selectPage(any(IPage.class), isNull());
    }
}
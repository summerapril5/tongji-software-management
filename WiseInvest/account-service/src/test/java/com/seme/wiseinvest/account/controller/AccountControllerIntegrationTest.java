package com.seme.wiseinvest.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seme.wiseinvest.account.domain.dto.CustomerDTO;
import com.seme.wiseinvest.account.service.CustomerService;
import com.seme.wiseinvest.common.domain.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest // Loads the full application context
@AutoConfigureMockMvc // Configures MockMvc
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON strings

    @MockBean // Mocks the CustomerService bean in the application context
    private CustomerService customerService;

    @Test
    void createAccount_success() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setPhoneNumber("13900139000");
        customerDTO.setIdNumber("987654321098765432");
        customerDTO.setPassword("securepass");
        customerDTO.setName("李四");
        customerDTO.setRiskLevel(2);

        // Mock the service layer to return a successful result
        when(customerService.createAccount(any(CustomerDTO.class))).thenReturn(12345L);

        MvcResult mvcResult = mockMvc.perform(post("/account/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0))) // Assuming your Result.success() sets code to 0
                .andExpect(jsonPath("$.message", is("操作成功"))) // And message to "操作成功"
                .andExpect(jsonPath("$.data", is(12345))) // Check the returned fund account ID
                .andReturn();

        // System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void createAccount_failure_accountExists() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setPhoneNumber("13800138000"); // Assume this account exists
        customerDTO.setIdNumber("123456789012345678");
        customerDTO.setPassword("password123");
        customerDTO.setName("张三");
        customerDTO.setRiskLevel(3);

        // Mock the service layer to return 0L, indicating account exists
        when(customerService.createAccount(any(CustomerDTO.class))).thenReturn(0L);

        mockMvc.perform(post("/account/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk()) // HTTP status is still OK
                .andExpect(jsonPath("$.code", is(1))) // Assuming your Result.error() sets code to 1 (or other non-zero)
                .andExpect(jsonPath("$.message", is("该手机号或身份证号已开户"))) // Check the error message
                .andExpect(jsonPath("$.data").doesNotExist()); // No data should be present on error
    }

    @Test
    void updateRiskLevel_success() throws Exception {
        when(customerService.updateRiskLevel(any(Long.class), any(Integer.class))).thenReturn(true);

        mockMvc.perform(patch("/account/risk_level")
                .param("fundAccount", "12345")
                .param("riskLevel", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.message", is("操作成功")));
    }

    @Test
    void updateRiskLevel_failure() throws Exception {
        when(customerService.updateRiskLevel(any(Long.class), any(Integer.class))).thenReturn(false);

        mockMvc.perform(patch("/account/risk_level")
                .param("fundAccount", "12345")
                .param("riskLevel", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(1))) // Assuming 1 for error
                .andExpect(jsonPath("$.message", is("更新失败")));
    }

    @Test
    void updateInfo_success() throws Exception {
        UpdateInfoDTO updateInfoDTO = new UpdateInfoDTO();
        updateInfoDTO.setFundAccount(12345L);
        updateInfoDTO.setPhoneNumber("13900139001");
        updateInfoDTO.setRiskLevel(4);

        when(customerService.updateInfo(any(UpdateInfoDTO.class))).thenReturn(true);

        mockMvc.perform(put("/account/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateInfoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.message", is("操作成功")));
    }

    @Test
    void updateInfo_failure_phoneExists() throws Exception {
        UpdateInfoDTO updateInfoDTO = new UpdateInfoDTO();
        updateInfoDTO.setFundAccount(12345L);
        updateInfoDTO.setPhoneNumber("13900139002"); // Assume this phone number is already in use by another customer
        updateInfoDTO.setRiskLevel(4);

        when(customerService.updateInfo(any(UpdateInfoDTO.class))).thenReturn(false);

        mockMvc.perform(put("/account/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateInfoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(1))) // Assuming 1 for error
                .andExpect(jsonPath("$.message", is("该手机号已有客户使用")));
    }

    @Test
    void getCustomers_success() throws Exception {
        List<CustomerVO> customerList = new ArrayList<>();
        CustomerVO customer1 = new CustomerVO(new Customer()); // Populate with test data
        customer1.setName("客户A");
        customerList.add(customer1);

        when(customerService.getCustomers(anyInt(), anyInt(), anyString())).thenReturn(customerList);

        mockMvc.perform(get("/account/customers")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .param("key", "客户"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", is("客户A")));
    }

    @Test
    void addBankcard_success() throws Exception {
        BankcardDTO bankcardDTO = new BankcardDTO();
        bankcardDTO.setFundAccount(12345L);
        bankcardDTO.setBankcardNumber("6222020000000000001");
        String tradingAccountId = "987654321";

        when(tradingAccountService.addBankcard(any(BankcardDTO.class))).thenReturn(tradingAccountId);

        mockMvc.perform(post("/account/add_bankcard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bankcardDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data", is(tradingAccountId)));
    }

    @Test
    void addBankcard_failure_cardExists() throws Exception {
        BankcardDTO bankcardDTO = new BankcardDTO();
        bankcardDTO.setFundAccount(12345L);
        bankcardDTO.setBankcardNumber("6222020000000000002"); // Assume this card exists

        when(tradingAccountService.addBankcard(any(BankcardDTO.class))).thenReturn("该卡号已有交易账户");

        mockMvc.perform(post("/account/add_bankcard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bankcardDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(1)))
                .andExpect(jsonPath("$.message", is("该卡号已有交易账户")));
    }

    @Test
    void deleteBankcard_success() throws Exception {
        when(tradingAccountService.deleteBankcard(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/account/delete_bankcard")
                .param("tradingAccountId", "987654321"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.message", is("操作成功")));
    }

    @Test
    void deleteBankcard_failure() throws Exception {
        when(tradingAccountService.deleteBankcard(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/account/delete_bankcard")
                .param("tradingAccountId", "987654321"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(1)))
                .andExpect(jsonPath("$.message", is("删除失败")));
    }

    @Test
    void getBankcards_success() throws Exception {
        List<BankcardVO> bankcards = new ArrayList<>();
        // Populate bankcards with test data
        BankcardVO card1 = new BankcardVO(new TradingAccount(1L, 12345L, "6222020000000000001", false));
        bankcards.add(card1);

        when(tradingAccountService.getBankcards(anyLong())).thenReturn(bankcards);

        mockMvc.perform(get("/account/bankcards")
                .param("fundAccount", "12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.data[0].bankcardNumber", is("6222020000000000001")));
    }

    @Test
    void getBankcard_success() throws Exception {
        Bankcard bankcard = new Bankcard("6222020000000000001", 200.0);
        when(tradingAccountService.getBankcardByTradingAccountId(anyLong())).thenReturn(bankcard);

        mockMvc.perform(post("/account/bankcard") // Note: This is a POST in controller, but semantically a GET
                .param("tradingAccountId", "987654321"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankcardNumber", is("6222020000000000001")))
                .andExpect(jsonPath("$.balance", is(200.0)));
    }

    @Test
    void getBalance_success() throws Exception {
        when(tradingAccountService.getBalance(anyString())).thenReturn(500.75);

        mockMvc.perform(get("/account/balance")
                .param("bankcardNumber", "6222020000000000001"))
                .andExpect(status().isOk())
                .andExpect(content().string("500.75"));
    }

    @Test
    void updateBalance_success() throws Exception {
        Bankcard bankcard = new Bankcard("6222020000000000001", 1000.0);
        when(tradingAccountService.updateBalance(any(Bankcard.class))).thenReturn(true);

        mockMvc.perform(patch("/account/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bankcard)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void getTotal_success() throws Exception {
        when(customerService.count()).thenReturn(150L);

        mockMvc.perform(get("/account/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data", is(150)));
    }

    @Test
    void getTradingAccounts_success() throws Exception {
        List<String> tradingAccountIds = List.of("acc1", "acc2");
        when(tradingAccountService.getTradingAccounts(anyLong())).thenReturn(tradingAccountIds);

        mockMvc.perform(get("/account/trading_accounts")
                .param("fundAccount", "12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data[0]", is("acc1")))
                .andExpect(jsonPath("$.data[1]", is("acc2")));
    }
}
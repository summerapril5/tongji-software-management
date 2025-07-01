package com.seme.wiseinvest.account.controller;

import com.seme.wiseinvest.account.domain.Customer;
import com.seme.wiseinvest.account.domain.dto.BankcardDTO;
import com.seme.wiseinvest.account.domain.dto.UpdateInfoDTO;
import com.seme.wiseinvest.account.service.AdminService;
import com.seme.wiseinvest.account.service.TradingAccountService;
import com.seme.wiseinvest.api.Bankcard;
import com.seme.wiseinvest.common.domain.Result;
import com.seme.wiseinvest.account.domain.dto.CustomerDTO;
import com.seme.wiseinvest.account.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final CustomerService customerService;
    private final AdminService adminService;
    private final TradingAccountService tradingAccountService;

    @PostMapping("/create")
    public Result createAccount(@RequestBody CustomerDTO customerDTO){
        long result = customerService.createAccount(customerDTO);
        return result == 0L ? Result.error("该手机号或身份证号已开户") : Result.success(result);
    }

    @PatchMapping("/risk_level")
    public Result updateRiskLevel(@RequestParam Long fundAccount, @RequestParam int riskLevel){
        boolean result = customerService.updateRiskLevel(fundAccount, riskLevel);
        return result ? Result.success() : Result.error("更新失败");
    }

    @PutMapping("/info")
    public Result updateInfo(@RequestBody UpdateInfoDTO updateInfoDTO){
        boolean result = customerService.updateInfo(updateInfoDTO);
        return result ? Result.success() : Result.error("该手机号已有客户使用");
    }

    @GetMapping("/customers")
    public Result getCustomers(@RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize,
                               @RequestParam(defaultValue = "") String key){
        return Result.success(customerService.getCustomers(pageNum, pageSize, key));
    }

    @PostMapping("/add_bankcard")
    public Result addBankcard(@RequestBody BankcardDTO bankcardDTO){
        String result = tradingAccountService.addBankcard(bankcardDTO);
        if(result.length() > 15)
            return Result.success(result);
        return Result.error(result);
    }

    @DeleteMapping("/delete_bankcard")
    public Result deleteBankcard(@RequestParam String tradingAccountId){
        return tradingAccountService.deleteBankcard(Long.parseLong(tradingAccountId)) ? Result.success() : Result.error("删除失败");
    }

    @GetMapping("/bankcards")
    public Result getBankcards(@RequestParam String fundAccount){
        return Result.success(tradingAccountService.getBankcards(Long.parseLong(fundAccount)));
    }

    @PostMapping("/bankcard")
    public Bankcard getBankcard(@RequestParam String tradingAccountId){
        return tradingAccountService.getBankcardByTradingAccountId(Long.parseLong(tradingAccountId));
    }

    @GetMapping("/balance")
    public double getBalance(@RequestParam String bankcardNumber){
        return tradingAccountService.getBalance(bankcardNumber);
    }

    @PatchMapping("/balance")
    public boolean updateBalance(@RequestBody Bankcard bankcard){
        return tradingAccountService.updateBalance(bankcard);
    }

//    @PostMapping("/create_admin")
//    public boolean createAdmin(@RequestBody Admin user){
//        return adminService.createAdmin(user);
//    }

    @GetMapping("/total")
    public Result getTotal(){
        return Result.success(customerService.count());
    }

    @GetMapping("/trading_accounts")
    public Result getTradingAccounts(@RequestParam String fundAccount){
        return Result.success(tradingAccountService.getTradingAccounts(Long.parseLong(fundAccount)));
    }
    @GetMapping("/customer/byFundAccount")
    public Result getCustomerByFundAccount(@RequestParam Long fundAccount) {
        Customer customer = customerService.findCustomerByFundAccount(fundAccount);
        if (customer != null) {
            // Customer 对象会被放入 Result 的 Object data 字段
            return Result.success(customer);
        }
        return Result.error("未找到对应资金账号的客户信息");
    }
}

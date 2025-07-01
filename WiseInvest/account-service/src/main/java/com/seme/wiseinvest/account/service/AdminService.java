package com.seme.wiseinvest.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seme.wiseinvest.account.domain.Admin;
import com.seme.wiseinvest.account.domain.User;

public interface AdminService extends IService<Admin> {
    boolean createAdmin(Admin admin);
}

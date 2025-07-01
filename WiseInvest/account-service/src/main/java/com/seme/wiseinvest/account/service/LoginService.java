package com.seme.wiseinvest.account.service;

import com.seme.wiseinvest.account.domain.dto.LoginDTO;

public interface LoginService {
    String checkPassword(LoginDTO loginDTO);

    boolean isVaildPhoneNumber(String phoneNumber);

    boolean isVaildAdminPhoneNumber(String phoneNumber);

    boolean changePassword(LoginDTO loginDTO);
}


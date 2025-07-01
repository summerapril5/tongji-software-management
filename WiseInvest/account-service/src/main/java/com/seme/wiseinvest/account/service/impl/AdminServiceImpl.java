package com.seme.wiseinvest.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seme.wiseinvest.account.domain.Admin;
import com.seme.wiseinvest.account.mapper.AdminMapper;
import com.seme.wiseinvest.account.service.AdminService;
import com.seme.wiseinvest.common.utils.Md5Util;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>  implements AdminService  {
    @Override
    public boolean createAdmin(Admin admin) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone_number", admin.getPhoneNumber());
        if (count(queryWrapper) > 0)
            return false;
        admin.setPassword(Md5Util.getMD5String(admin.getPassword()));
        return save(admin);
    }
}

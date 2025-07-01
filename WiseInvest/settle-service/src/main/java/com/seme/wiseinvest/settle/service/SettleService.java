package com.seme.wiseinvest.settle.service;

import com.seme.wiseinvest.api.OurSystem;

public interface SettleService {
    OurSystem getSystem();
    boolean initializeDay();
    boolean receiveMarketData();
    boolean confirmSubscriptions();
    boolean confirmRedemptions();
    boolean stopDailyApplications();
    boolean exportData();

    OurSystem getNetValueSystem();
}

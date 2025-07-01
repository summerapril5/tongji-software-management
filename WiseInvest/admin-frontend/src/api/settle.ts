import request from '@/utils/request';

export function initializeDayService() {
    return request.post('/settle/settle/init');
}

export function receiveMarketDataService() {
    return request.post('/settle/settle/market');
}

export function confirmSubscriptionDataService() {
    return request.post('/settle/settle/subscription');
}

export function confirmRedemptionDataService() {
    return request.post('/settle/settle/redemption');
}

export function stopDailyApplicationsService() {
    return request.post('/settle/settle/stop');
}

export function exportDataService() {
    return request.post('/settle/settle/export');
}

export function getSystemService() {

    return request.get('/settle/settle');
}

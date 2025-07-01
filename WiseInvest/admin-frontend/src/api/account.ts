import request from '@/utils/request.ts'
import { ElMessage } from 'element-plus';

export function getCustomersByPageService(pageNum: number, pageSize: number, key:string) {
    return request.get('/account/account/customers', { params: { pageNum, pageSize,key } });
}

export function getCustomerTotalService() {
    return request.get('/account/account/total');
}

export function getBankcardsService(fundAccount: string) {
    return request.get('/account/account/bankcards', { params: { fundAccount } });
}
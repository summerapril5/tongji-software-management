import request from '@/utils/request.ts'

export function getTransactionsService(fundAccount: string) {

    return request.get('/transaction/query/transactions', { params: { fundAccount } });
}


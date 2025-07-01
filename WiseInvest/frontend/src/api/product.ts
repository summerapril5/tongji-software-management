// api/product.ts
import request from '@/utils/request';
import type { Product } from '@/types/product';

export function fetchProducts(page = 1, pageSize = 10) {
    return request.get('/product/product/list', { params: { page, pageSize } });
}

export function searchProducts(keyword:string) {
    return request.get(`/product/product/search/${keyword}`);
}

export function fetchNetValue(productId: number, date: string) {
    return request.get(`/product/product/netvalue/${productId}/${date}`);
}

export function fetchTransactionDate() {
    return request.get('/settle/settle/system/transaction-date');
}
export const fetchRecommendations = () => {
    return request.get('/recommendation/recommendations');
};

export interface SubscriptionDTO{
    tradingAccountId: string;
    fundAccount: string;
    productId: number;
    productName: string;
    amount: number;
}

export interface RedemptionDTO{
    tradingAccountId: string;
    fundAccount: string;
    productId: number;
    productName: string;
    shares: number;
}
export interface Transaction {
    transactionId: string;
    tradingAccountId: string;
    fundAccount: string;
    productId: number;
    productName: string;
    amount: number;
    shares: number;
    applicationTime: string;
    cancel: boolean;
    canCancel: boolean;
    subscribe: boolean;
}
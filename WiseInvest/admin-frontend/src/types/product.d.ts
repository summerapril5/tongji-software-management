// Product - 用于表示产品信息，既可用于数据传输也可用于视图展示
export interface Product {
    productId?: number;         // 自动递增的主键
    productName: string;       // 产品名称
    productType: string;       // 产品类型
    riskLevel?: number;        // 风险等级，默认值为4，因此是可选的
    productStatus?: number;    // 产品状态，默认值为0，因此是可选的
}

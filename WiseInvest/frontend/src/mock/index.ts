// 模拟数据
export const mockData = {
    token: 'mock-token-for-development',
    customer: {
      phoneNumber: '13800138000',
      riskLevel: 2,
      name: '测试用户',
      fundAccount: '10000001'
    },
    bankcards: [
      {
        tradingAccount: 'T10000001',
        bankcardNumber: '6222021234567890123'
      },
      {
        tradingAccount: 'T10000002',
        bankcardNumber: '6217001234567890'
      }
    ],
    products: [
      {
        productId: 1,
        productName: '稳健成长基金',
        productType: '混合型',
        riskLevel: 2,
        netWorth: 1.2345,
        dailyGrowth: 0.0123,
        weeklyGrowth: 0.0234,
        monthlyGrowth: 0.0345,
        yearlyGrowth: 0.1234,
        productStatus: 1
      },
      {
        productId: 2,
        productName: '创新动力基金',
        productType: '股票型',
        riskLevel: 4,
        netWorth: 2.3456,
        dailyGrowth: -0.0056,
        weeklyGrowth: 0.0345,
        monthlyGrowth: 0.0567,
        yearlyGrowth: 0.2345,
        productStatus: 1
      },
      {
        productId: 3,
        productName: '安心收益债券基金',
        productType: '债券型',
        riskLevel: 1,
        netWorth: 1.0567,
        dailyGrowth: 0.0012,
        weeklyGrowth: 0.0045,
        monthlyGrowth: 0.0123,
        yearlyGrowth: 0.0456,
        productStatus: 1
      },
      {
        productId: 4,
        productName: '科技先锋基金',
        productType: '股票型',
        riskLevel: 5,
        netWorth: 3.4567,
        dailyGrowth: 0.0234,
        weeklyGrowth: 0.0456,
        monthlyGrowth: 0.0789,
        yearlyGrowth: 0.3456,
        productStatus: 1
      },
      {
        productId: 5,
        productName: '均衡配置基金',
        productType: '混合型',
        riskLevel: 3,
        netWorth: 1.5678,
        dailyGrowth: 0.0045,
        weeklyGrowth: 0.0123,
        monthlyGrowth: 0.0234,
        yearlyGrowth: 0.1567,
        productStatus: 1
      }
    ],
    transactions: [
      {
        transactionId: 'TX20230601001',
        tradingAccountId: 'T10000001',
        fundAccount: '10000001',
        productId: 1,
        productName: '稳健成长基金',
        amount: 10000,
        shares: 8100.45,
        applicationTime: '2023-06-01 10:00:00',
        cancel: false,
        canCancel: false,
        subscribe: true
      },
      {
        transactionId: 'TX20230615001',
        tradingAccountId: 'T10000001',
        fundAccount: '10000001',
        productId: 2,
        productName: '创新动力基金',
        amount: 20000,
        shares: 8526.77,
        applicationTime: '2023-06-15 14:30:00',
        cancel: false,
        canCancel: false,
        subscribe: true
      },
      {
        transactionId: 'TX20230620001',
        tradingAccountId: 'T10000001',
        fundAccount: '10000001',
        productId: 3,
        productName: '安心收益债券基金',
        amount: 5000,
        shares: 4731.52,
        applicationTime: '2023-06-20 09:15:00',
        cancel: false,
        canCancel: false,
        subscribe: true
      },
      {
        transactionId: 'TX20230710001',
        tradingAccountId: 'T10000001',
        fundAccount: '10000001',
        productId: 1,
        productName: '稳健成长基金',
        amount: 0,
        shares: 4000,
        applicationTime: '2023-07-10 11:20:00',
        cancel: false,
        canCancel: false,
        subscribe: false
      },
      {
        transactionId: 'TX20230725001',
        tradingAccountId: 'T10000001',
        fundAccount: '10000001',
        productId: 2,
        productName: '创新动力基金',
        amount: 15000,
        shares: 6394.30,
        applicationTime: '2023-07-25 15:45:00',
        cancel: false,
        canCancel: true,
        subscribe: true
      },
      {
        transactionId: 'TX20230801001',
        tradingAccountId: 'T10000001',
        fundAccount: '10000001',
        productId: 4,
        productName: '科技先锋基金',
        amount: 30000,
        shares: 8678.91,
        applicationTime: '2023-08-01 10:30:00',
        cancel: true,
        canCancel: false,
        subscribe: true
      }
    ]
  };
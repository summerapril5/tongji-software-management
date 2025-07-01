export interface CustomerDTO {
    phoneNumber: string;
    riskLevel: number;
    name: string;
    password: string;
    idNumber: string;
  }
  
  export interface BankcardDTO {
    fundAccount: string;
    bankcardNumber: string;
  }
  
  export interface BankcardVO {
    tradingAccount: string;
    bankcardNumber: string;
  }
  
  export interface UpdateInfoDTO {
    phoneNumber: string;
    riskLevel: number;
    fundAccount: string;
  }
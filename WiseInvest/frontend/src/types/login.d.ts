export interface LoginDTO {
    phoneNumber: string;
    password: string;
    userType: 1;
  }
  
  export interface CustomerInfo {
    phoneNumber: string;
    riskLevel: number;
    name: string;
    fundAccount: string;
  }
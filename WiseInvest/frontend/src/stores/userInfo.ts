import { defineStore } from 'pinia';
import { ref, type Ref } from 'vue';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import type { CustomerInfo } from '@/types/login';

export const useUserInfoStore = defineStore(
  'info',
  () => {
    const info = ref({
        phoneNumber: "",
        riskLevel: 0,
        name: "",
        fundAccount: ""
    });
    const setInfo = (data: CustomerInfo) => {
      info.value = data;
    };
    const removeInfo = () => {
      info.value = {
        phoneNumber: "",
        riskLevel: 0,
        name: "",
        fundAccount: ""
      };
    };
    return {
      info,
      setInfo,
      removeInfo,
    };
  },
  {
    persist: true
  }
);
import { defineStore } from 'pinia';
import { ref, type Ref } from 'vue';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import type { AdminInfo } from '@/types/login';

export const useUserInfoStore = defineStore(
  'info',
  () => {
    const info = ref({
        phoneNumber: "",
        adminAccount: ""
    });
    const setInfo = (data: AdminInfo) => {
      info.value = data;
    };
    const removeInfo = () => {
      info.value = {
        phoneNumber: "",
        adminAccount: ""
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
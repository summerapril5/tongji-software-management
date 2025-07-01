<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="logo-container">
        <img src="@/assets/logo.png" alt="WiseInvest" class="logo-img"/>
        <h2 class="login-title">WiseInvest Admin</h2>
      </div>
      <el-form :model="loginForm" :rules="rules" label-position="top">
        <el-form-item label="手机号" prop="phoneNumber">
          <el-input v-model="loginForm.phoneNumber" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <div class="button-group">
          <el-button type="primary" @click="login" class="login-button">登录</el-button>
          <el-button type="default" @click="router.push('/resetpw')" class="forget-button">忘记密码</el-button>
        </div>
      </el-form>  
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'; // 引入 ref 来创建响应式数据
import { ElMessage, ElForm, ElFormItem, ElInput, ElButton, ElCard } from 'element-plus'; // 引入ElementPlus组件
import { loginService } from '@/api/login';
import type { LoginDTO, AdminInfo } from '@/types/login';
import { useTokenStore } from '@/stores/token';
import { useUserInfoStore } from '@/stores/userInfo';
import {jwtDecode } from 'jwt-decode';
import { useRouter } from 'vue-router';

// 定义响应式变量
const loginForm = ref({
  phoneNumber: '',
  password: '',
  userType: 2,
} as LoginDTO);

const rules = {
  phoneNumber: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号必须是11位数字', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
  ],
};

const tokenStore = useTokenStore();
const userInfoStore = useUserInfoStore();
const router = useRouter();

const login = async () => {
  const res = await loginService(loginForm.value);
  tokenStore.setToken(res.data);
  const decode = jwtDecode(res.data) as {claims: AdminInfo};
  userInfoStore.setInfo(decode.claims);
  ElMessage.success("登录成功");
  router.push('/info');
};
</script>

  
<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #9cb7e7;
}
.login-card {
  width: 400px;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.logo-container {
  display: flex;
  justify-content:space-around;
  align-items: center;
  margin-bottom: 20px;
}
.logo-img {
  justify-self: left;
  height: 60px;
  object-fit: contain;
}
.login-title {
  font-size: 30px;
  color: #344dbe;
  margin-bottom: 30px;
  font-weight: bold;
}
.button-group {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}
.login-button {
  flex: 1;
  background-color: #1a73e8;
}
.forget-button {
  flex: 1;
  color: #1a73e8;
  border-color: #1a73e8;
  background-color: #f8f9fa;
}
:deep(.el-input__wrapper) {
  background-color: #f8f9fa;
}
:deep(.el-form-item__label) {
  color: #333;
  font-size: 14px;
  margin-bottom: 8px;
}
:deep(.el-button) {
  height: 40px;
  font-size: 14px;
}
:deep(.el-input__inner) {
  height: 40px;
}
</style>

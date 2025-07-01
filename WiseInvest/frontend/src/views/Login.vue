<template>
  <!-- 登录页面容器 -->
  <div class="login-container">
    <!-- 卡片布局用于登录表单 -->
    <el-card class="login-card">
      <!-- logo区域 -->
      <div class="logo-container">
        <img src="@/assets/logo.png" alt="WiseInvest" class="logo-img"/>
        <h2 class="login-title">用户登录</h2>
      </div>

      <!-- 登录表单 -->
      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          label-position="top"
      >
        <!-- 手机号输入框 -->
        <el-form-item prop="phoneNumber">
          <template #label>
            <span class="required-label">手机号</span>
          </template>
          <el-input v-model="loginForm.phoneNumber" placeholder="请输入您的手机号" />
        </el-form-item>

        <!-- 密码输入框 -->
        <el-form-item prop="password">
          <template #label>
            <span class="required-label">密码</span>
          </template>
          <el-input v-model="loginForm.password" type="password" placeholder="请输入您的密码" />
        </el-form-item>

        <!-- 按钮组：登录、忘记密码 -->
        <div class="button-group">
          <el-button type="primary" @click="login" class="login-button">登录</el-button>
          <el-button type="default" @click="router.push('/resetpw')" class="forget-button">忘记密码</el-button>
        </div>

        <!-- 注册按钮（开户） -->
        <el-button type="success" @click="router.push('/create')" class="register-button">开户</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'; // 引入 ref 来创建响应式数据
import { ElMessage, ElForm, ElFormItem, ElInput, ElButton, ElCard } from 'element-plus'; // 引入ElementPlus组件
import { loginService } from '@/api/login'; // 引入登录接口
import type { LoginDTO, CustomerInfo } from '@/types/login'; // 引入类型定义
import { useTokenStore } from '@/stores/token'; // 引入token状态管理
import { useUserInfoStore } from '@/stores/userInfo'; // 引入用户信息状态管理
import { jwtDecode } from 'jwt-decode'; // 引入 JWT 解码工具
import { useRouter } from 'vue-router'; // 引入路由功能

// 定义登录表单数据
const loginForm = ref({
  phoneNumber: '',
  password: '',
  userType: 1,
} as LoginDTO);

// 定义表单验证规则
const rules = {
  phoneNumber: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号必须是11位数字', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
  ],
};

// 使用Pinia中的store
const tokenStore = useTokenStore();
const userInfoStore = useUserInfoStore();
const router = useRouter();

// 登录函数
const login = async () => {
  const res = await loginService(loginForm.value); // 调用登录接口
  tokenStore.setToken(res.data); // 存储token
  const decode = jwtDecode(res.data) as {claims: CustomerInfo}; // 解码JWT获取用户信息
  userInfoStore.setInfo(decode.claims); // 存储用户信息
  ElMessage.success("登录成功"); // 显示登录成功提示
  router.push('/info'); // 跳转到用户信息页面
};
</script>

<style scoped>
/* 页面整体布局 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #9cb7e7;
}

/* 登录卡片样式 */
.login-card {
  width: 400px;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* logo区域布局 */
.logo-container {
  display: flex;
  justify-content:space-around;
  align-items: center;
  margin-bottom: 20px;
}

/* logo图片样式 */
.logo-img {
  justify-self: left;
  height: 60px;
  object-fit: contain;
}

/* 标题样式 */
.login-title {
  font-size: 30px;
  color: #344dbe;
  margin-bottom: 30px;
  font-weight: bold;
}

/* 必填项标签前加红色星号 */
.required-label::before {
  content: '';
  color: #f56c6c;
  margin-right: 4px;
}

/* 按钮组布局 */
.button-group {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

/* 登录按钮样式 */
.login-button {
  flex: 1;
  background-color: #1a73e8;
}

/* 忘记密码按钮样式 */
.forget-button {
  flex: 1;
  color: #1a73e8;
  border-color: #1a73e8;
  background-color: #f8f9fa;
}

/* 注册按钮样式 */
.register-button {
  width: 100%;
  background-color: #34a853;
}

/* 深度选择器，设置输入框背景色 */
:deep(.el-input__wrapper) {
  background-color: #f8f9fa;
}

/* 设置表单标签样式 */
:deep(.el-form-item__label) {
  color: #333;
  font-size: 14px;
  margin-bottom: 8px;
}

/* 设置按钮统一高度与字体 */
:deep(.el-button) {
  height: 40px;
  font-size: 14px;
}

/* 设置输入框统一高度 */
:deep(.el-input__inner) {
  height: 40px;
}
</style>

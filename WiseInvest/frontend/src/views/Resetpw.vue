<template>
  <!-- 找回密码页面容器 -->
  <div class="reset-password-container">
    <!-- 卡片式表单容器 -->
    <el-card class="reset-password-form">
      <!-- 顶部logo和标题 -->
      <div class="logo-container">
        <img src="@/assets/logo.png" alt="WiseInvest" class="logo-img"/>
        <h2 class="reset-title">找回密码</h2>
      </div>
      <!-- 表单区域 -->
      <el-form :model="form" :rules="rules">
        <!-- 手机号输入项 -->
        <el-form-item label="手机号" prop="phoneNumber">
          <el-input v-model="form.phoneNumber" placeholder="请输入手机号" />
        </el-form-item>
        <!-- 验证码输入项及发送按钮 -->
        <el-form-item label="验证码" prop="code">
          <el-input v-model="form.code" placeholder="请输入验证码">
            <template #append>
              <el-button @click="sendCodeService(form.phoneNumber)" type="primary" size="small">发送验证码</el-button>
            </template>
          </el-input>
        </el-form-item>
        <!-- 新密码输入项 -->
        <el-form-item label="新密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入新密码" />
        </el-form-item>
        <!-- 确认密码输入项 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请确认密码" />
        </el-form-item>
        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" style="width: 100%">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'; // 引入 ref 创建响应式变量
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage } from 'element-plus'; // 引入 Element Plus 组件
import { resetPwService, sendCodeService } from '@/api/login'; // 引入找回密码与验证码发送接口
import type { LoginDTO } from '@/types/login'; // 引入登录数据类型
import { useRouter } from 'vue-router'; // 引入路由功能

// 定义表单数据类型接口
interface FormData {
  phoneNumber: string;
  code: string;
  password: string;
  confirmPassword: string;
}

// 定义响应式表单数据
const form = ref<FormData>({
  phoneNumber: '',
  code: '',
  password: '',
  confirmPassword: '',
});

const router = useRouter(); // 获取路由实例

// 表单验证规则
const rules = {
  phoneNumber: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号必须是11位数字', trigger: 'blur' },
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 16, message: '密码长度在6到16位之间', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
  ]
};

// 提交表单处理函数
const handleSubmit = async () => {
  // 密码为空校验
  if(form.value.password === ''){
    ElMessage.warning('密码不可以为空');
    return;
  }
  // 验证码错误校验（此处验证码写死为123，仅用于测试）
  if(form.value.code !== '123'){
    ElMessage.error('验证码错误');
    return;
  }
  // 确认密码一致性校验
  if(form.value.password === form.value.confirmPassword){
    const data: LoginDTO = {
      phoneNumber: form.value.phoneNumber,
      password: form.value.password,
      userType: 1,
    }
    // 调用密码重置接口
    await resetPwService(data);
    ElMessage.success('密码重置成功');
    router.push('/login'); // 重置成功后跳转登录页
  }else{
    // 密码不一致提示并清空密码字段
    ElMessage.error('请输入两次一致的密码');
    form.value.password = '';
    form.value.confirmPassword = '';
  }
};
</script>

<style scoped>
/* 页面容器样式 */
.reset-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #9cb7e7;
}

/* 表单卡片样式 */
.reset-password-form {
  width: 400px;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* logo容器样式 */
.logo-container {
  display: flex;
  justify-content: space-around;
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
.reset-title {
  font-size: 30px;
  color: #344dbe;
  margin-bottom: 30px;
  font-weight: bold;
}

/* 表单控件样式：输入框背景色 */
:deep(.el-input__wrapper) {
  background-color: #f8f9fa;
}

/* 表单标签样式 */
:deep(.el-form-item__label) {
  color: #333;
  font-size: 14px;
  margin-bottom: 8px;
}

/* 按钮统一高度与字体 */
:deep(.el-button) {
  height: 40px;
  font-size: 14px;
}

/* 输入框统一高度 */
:deep(.el-input__inner) {
  height: 40px;
}
</style>

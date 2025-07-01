<template>
  <!-- 页面整体容器 -->
  <div class="create-account-container">
    <!-- 卡片容器，动态 class 控制是否展示风险评估 -->
    <div class="card-container" :class="{ 'show-assessment': isTestLevel }">
      <!-- 注册信息卡片 -->
      <el-card class="register-card">
        <div class="card-header">
          <!-- Logo -->
          <img src="@/assets/logo.png" alt="WiseInvest" class="logo-img"/>
          <h2>用户注册</h2>
        </div>

        <!-- 表单组件，绑定模型与校验规则 -->
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="form-container">
          <!-- 姓名 -->
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入姓名"></el-input>
          </el-form-item>

          <!-- 手机号 -->
          <el-form-item label="手机号" prop="phoneNumber">
            <el-input v-model="form.phoneNumber" placeholder="请输入手机号"></el-input>
          </el-form-item>

          <!-- 验证码 -->
          <el-form-item label="验证码" prop="code">
            <el-input v-model="form.code" placeholder="请输入验证码">
              <!-- 右侧插槽：发送验证码按钮 -->
              <template #append>
                <el-button @click="sendCodeService" type="primary" size="small">发送验证码</el-button>
              </template>
            </el-input>
          </el-form-item>

          <!-- 身份证号 -->
          <el-form-item label="身份证号码" prop="idNumber">
            <el-input v-model="form.idNumber" placeholder="请输入身份证号码"></el-input>
          </el-form-item>

          <!-- 密码 -->
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" placeholder="请输入密码" type="password" maxlength="16"></el-input>
          </el-form-item>

          <!-- 确认密码 -->
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" placeholder="请确认密码" type="password" ></el-input>
          </el-form-item>

          <!-- 是否进行风险评估 -->
          <el-form-item>
            <el-button type="success" @click="isTestLevel = !isTestLevel" class="assessment-button">
              {{ isTestLevel ? "跳过风险评估" : "进行风险评估" }}
            </el-button>
          </el-form-item>

          <!-- 是否添加银行卡 -->
          <el-form-item>
            <el-button type="warning" @click="isBankcardNumber = !isBankcardNumber" class="bankcard-button">
              {{isBankcardNumber ? "跳过添加银行卡" : "添加银行卡"}}
            </el-button>
          </el-form-item>

          <!-- 银行卡号（条件渲染） -->
          <el-form-item v-if="isBankcardNumber" label="银行卡号" prop="bankcardNumber">
            <el-input v-model="form.bankcardNumber" placeholder="请输入银行卡号"></el-input>
          </el-form-item>

          <!-- 提交开户按钮 -->
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" class="submit-button">确定开户</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 风险评估组件（条件展示） -->
      <div v-if="isTestLevel" class="assessment-container">
        <RiskAssessment @update:level="handleLevelChange" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import { ElMessage, ElMessageBox } from 'element-plus';
  import { useRouter } from 'vue-router';
  import RiskAssessment from '../components/RiskAssessment.vue';
  import { createAccountService, addBankcardService } from '../api/account';
  // 表单数据模型
  const form = ref({
    phoneNumber: '',
    password: '',
    idNumber: '',
    name: '',
    riskLevel: 0,
    confirmPassword: '',
    bankcardNumber: '',
    code: '',
  });

  const router = useRouter();
  const isTestLevel = ref(true); 
  const isBankcardNumber = ref(true); 

  // 表单验证规则
  const rules = {
    phoneNumber: [
      { required: true, message: '手机号不能为空', trigger: 'blur' },
      { pattern: /^1\d{10}$/, message: '手机号必须是11位数字', trigger: 'blur' },
    ],
    password: [
      { required: true, message: '密码不能为空', trigger: 'blur' },
      { min: 3, max: 16, message: '密码长度在6到16位之间', trigger: 'blur' }, 
    ],
    idNumber: [
      { required: true, message: '身份证号码不能为空', trigger: 'blur' },
      { pattern: /^\d{17}(\d|X|x)$/, message: '请输入有效的身份证号码', trigger: 'blur' }
    ],
    name: [
      { required: true, message: '姓名不能为空', trigger: 'blur' },
    ],
    bankcardNumber: [
      { required: true, message: '银行卡号不能为空', trigger: 'blur' },
      { pattern: /^\d{16}$/, message: '银行卡号必须是16位数字', trigger: 'blur' }, 
    ],
    code: [
      { required: true, message: '请输入验证码', trigger: 'blur' }
    ],
    confirmPassword: [
      { required: true, message: '请确认密码', trigger: 'blur' },
      {
        validator: (rule: any, value: any, callback: any) => {
          if (value === '') {
            callback(new Error('请再次输入密码'));
          } else if (value !== form.value.password) {
            callback(new Error('两次输入密码不一致!'));
          } else {
            callback();
          }
        },
        trigger: 'blur'
      }
    ]
  };
  const formRef = ref();

  const sendCodeService =  () => {
    setTimeout(() => {
      ElMessage.success('验证码已发送');
      }, 500);
  };

  const handleSubmit = async() => {
    formRef.value.validate(async (valid: boolean) => {
      if (valid) {
        if(form.value.password === ''){
          ElMessage.warning('密码不可以为空');
          return;
        }
        if(form.value.code !== '123'){
          ElMessage.error('验证码错误');
          return;
        }
        if(form.value.password !== form.value.confirmPassword)
          ElMessage.error('两次密码不一致');
        else
          ElMessageBox.confirm('是否确认开户？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }).then(async () => {
            let res = await createAccountService({
                phoneNumber: form.value.phoneNumber,
                password: form.value.password,
                name: form.value.name,
                idNumber: form.value.idNumber,
                riskLevel: isTestLevel.value ? form.value.riskLevel : 0,
            });
            ElMessage.success('开户成功');
            router.push('/login');
            if(isBankcardNumber)
              await addBankcardService({
                bankcardNumber: form.value.bankcardNumber,
                fundAccount: res.data,
            });
          })
      }else{
        ElMessage.error('请输入正确的信息');
      }
    });
  };

  const handleLevelChange = (level: number) => {
    form.value.riskLevel = level;
  };
</script>

<style scoped>
.create-account-container {
  padding: 20px;
  background-color: #e6f0ff;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.card-container {
  display: flex;
  justify-content: center;
  gap:10px;
  transition: all 0.3s ease;
  width: 100%;
  max-width: 1400px;
}

.card-container.show-assessment {
  justify-content: space-between;
}

.register-card {
  width: 700px;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.card-header {
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin-bottom: 30px;
}

.logo-img {
  height: 60px;
  margin-right: 15px;
}

.card-header h2 {
  font-size: 24px;
  color: #1a73e8;
  margin: 0;
}

.form-container {
  position: relative;
  display:flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;
}

.assessment-container {
  width: 700px;
  transition: all 0.3s ease;
  opacity: 0;
  transform: translateX(50px);
  animation: slideIn 0.5s forwards;
}

@keyframes slideIn {
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.assessment-button {
  position:relative;
  width: 50%;
  height:40px;
  font-size: 17px;
  background-color: #34a853;
  border-color: #34a853;
  margin-bottom: 15px;
  border-radius: 10px;
  left:25%;
  border:none;
}

.bankcard-button {
  position:relative;
  left:25%;
  width: 50%;
  height:40px;
  font-size: 17px;
  background-color: #ff9800;
  border-color: #ff9800;
  margin-bottom: 15px;
  border-radius: 10px;
  border:none;
}

.submit-button {
  position:relative;
  left:25%;
  width: 50%;
  height:40px;
  font-size: 17px;
  background-color: #1a73e8;
  border-color: #1a73e8;
  border-radius: 10px;
  border:none;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  background-color: #f8f9fa;
}
</style>

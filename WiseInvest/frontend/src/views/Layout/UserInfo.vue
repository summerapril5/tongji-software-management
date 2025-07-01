<script lang="ts" setup>
import { ref, computed} from 'vue';
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage, ElMessageBox } from 'element-plus';
import RiskAssessment from '@/components/RiskAssessment.vue';
import { addBankcardService,UpdateInfoService,getBankcardsService,deleteBankcardService } from '@/api/account';
import { useUserInfoStore } from '@/stores/userInfo';
import type { CustomerInfo } from '@/types/login';
import type { BankcardVO } from '@/types/account';
import { useRouter } from 'vue-router';
import { useTokenStore } from '@/stores/token';

const tokenStore = useTokenStore();
const userInfoStore = useUserInfoStore();
const userInfo = userInfoStore.info;
const tradingAccounts = ref<BankcardVO[]>([]);
const router = useRouter();

const getBankcards = async () => {
  if(userInfo.fundAccount === ''){
    router.push('/login');
    return ElMessage.warning('请先登录');
  }
  const res = await getBankcardsService(userInfo.fundAccount);
  tradingAccounts.value = res.data;
}

getBankcards();

// 用来控制修改状态
const isEditing = ref(false);
const isTestLevel = ref(false);
const form = ref({ ...userInfo });
const bankcardNumber = ref('');
const code = ref('');

const handleLevelChange = (level: number) => {
  form.value.riskLevel = level;
};

const handleSubmit = async () => {
  if(code.value !== '123')
    return ElMessage.error('验证码错误');
  if(!/^\d{11}$/.test(form.value.phoneNumber)){
    ElMessage.warning('手机号必须为11位数字');
    return;
  }
  await UpdateInfoService({
    phoneNumber: form.value.phoneNumber,
    riskLevel: isTestLevel ? form.value.riskLevel : userInfo.riskLevel,
    fundAccount: userInfo.fundAccount,
  });
  ElMessage.success('个人信息修改成功');
  tokenStore.removeToken();
  userInfoStore.removeInfo();
  router.push('/login');
};

const cancelEdit = () => {
  form.value = { ...userInfo };
  isEditing.value = false;
  isTestLevel.value = false;
};

const deleteAccount = async (tradingAccount: string) => {
  await deleteBankcardService(tradingAccount);
  tradingAccounts.value = tradingAccounts.value.filter((account) => account.tradingAccount !== tradingAccount);
  ElMessage.success('银行卡号删除成功');
};

const Level = (level : number) => {
  switch (level) {
    case 0:
      return '低风险的保守型';
    case 1:
      return '中低风险的较稳健型';
    case 2:
      return '中风险的稳健型';
    case 3:
      return '中高风险的进取型';
    default:
      return '高风险的激进型';
  }
}

const sendCodeService =  () => {
  setTimeout(() => {
    ElMessage.success('验证码已发送');
  }, 500);
};

const addBankcard = async () => {
  if(!/^\d{16}$/.test(bankcardNumber.value)){
    ElMessage.warning('银行卡号必须为16位数字');
    return;
  }
  let res = await addBankcardService({
    fundAccount: userInfo.fundAccount,
    bankcardNumber: bankcardNumber.value,
  });
  tradingAccounts.value.push({
    tradingAccount: res.data,
    bankcardNumber: bankcardNumber.value
  });
  ElMessage.success('银行卡号添加成功');
}

const riskLevelDescription = computed(() => Level(form.value.riskLevel));

const logout = () => {
  ElMessageBox.confirm('是否确认退出登录？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    tokenStore.removeToken();
    userInfoStore.removeInfo();
    router.push('/login');
  })
}
</script>

<template>
  <div class="personal-info-container">
    <div style="display: flex; align-items: center; justify-content: center;margin-left: -300px">
      <img src="@/assets/logo.png" alt="WiseInvest" class="logo-img" style="width: 120px; height: 60px; margin-right: 10px;">
      <h3 style="padding-top: 0; padding-bottom: 0; margin: 0; color: #0b407ce0; font-size: 1.25em;">个人信息页面</h3>
    </div>
    <el-form :model="form" label-width="120px" class="form-container">
      <!-- 姓名 -->
      <el-form-item label="姓名">
        <el-input v-model="form.name" :disabled=true></el-input>
      </el-form-item>
      <!-- 基金账户 -->
      <el-form-item label="基金账户">
        <el-input v-model="form.fundAccount" :disabled=true></el-input>
      </el-form-item>
      <!-- 基金账户 -->
      <el-form-item label="风险等级">
        <el-input :value="riskLevelDescription" :disabled=true></el-input>
      </el-form-item>
      <!-- 手机号 -->
      <el-form-item label="手机号">
        <el-input v-model="form.phoneNumber" :disabled="!isEditing" placeholder="请输入手机号"></el-input>
      </el-form-item>
      <el-form-item label="验证码" v-if="isEditing">
        <el-input v-model="code" placeholder="请输入验证码">
          <template #append>
            <el-button type="primary" @click="sendCodeService">发送验证码</el-button>
          </template>
        </el-input>
      </el-form-item>
      <!-- 修改按钮 -->
      <el-form-item>
        <el-button v-if="!isEditing" type="primary" @click="isEditing = true;">修改</el-button>
        <el-button v-if="!isEditing" type="danger" @click="logout">退出登录</el-button>
        <el-button v-if="isEditing" type="danger" @click="isTestLevel = !isTestLevel">{{isTestLevel ? "取消" : "风险评估"}}</el-button>
      </el-form-item>
      <el-form-item v-if="isTestLevel">
        <RiskAssessment @update:level="handleLevelChange"/>
      </el-form-item>
      <el-form-item>
        <el-button v-if="isEditing" type="success" @click="handleSubmit">保存</el-button>
        <el-button v-if="isEditing" type="warning" @click="cancelEdit">取消</el-button>
      </el-form-item>
    </el-form>
    <!-- 基金账户和银行卡号的表格展示 -->
    <div style="margin-left: 130px;">
      <h3 style="padding: 10px; color: #0b407ce0; ">基金账户</h3>
      <el-table :data="tradingAccounts" style="width:46%; margin-top: 20px;">
        <el-table-column label="基金账户" prop="tradingAccount" width="200" align="center"></el-table-column>
        <el-table-column label="银行卡号" prop="bankcardNumber" width="200" align="center"></el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="scope">
            <el-button type="danger" size="small" style="width:40%" @click="deleteAccount(scope.row.tradingAccount)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-form label-width="120px" class="form-container" style="margin-top: 30px;">
      <el-form-item label="新增银行卡号">
        <el-input v-model="bankcardNumber" placeholder="请输入银行卡号">
          <template #append>
            <el-button type="success" @click="addBankcard">添加</el-button>
          </template>
        </el-input>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.personal-info-container {
  padding: 2px;
  background-color: #cad1d82e;
  min-height: 86.4vh;

}

.form-container {
  margin-left: 40px;
  width: 800px;
}

.el-button {
  margin-right: 10px;
}
</style>

<script lang="ts" setup>
import { ref } from 'vue';
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage, ElMessageBox } from 'element-plus';
import { useUserInfoStore } from '@/stores/userInfo';
import type { SubscriptionDTO } from '@/types/transaction';
import { useRouter,useRoute } from 'vue-router';
import { submitSubscriptionService } from '@/api/transaction';
import { getTradingAccountsService } from '@/api/account';

const form = ref({
  productId: 0,
  productName: '',
  tradingAccountId: '',
  subscriptionAmount: 0,
});

const transactionId = ref("");
const tradingAccounts = ref<string[]>([]);
const userInfoStore = useUserInfoStore();
const route = useRoute();
const router = useRouter();
const userInfo = userInfoStore.info;
const dialogVisible = ref(false);
const getProductInfo = () => {
  form.value.productId = route.query.productId as unknown as number;
  form.value.productName = route.query.productName as string;
  console.log(form.value.productId);
  console.log(form.value.productName);
  if(form.value.productId === undefined || form.value.productName === undefined){
    router.push('/product');
    return ElMessage.warning('请先选择产品');
  }
};
const submitSubscription = async () => {
  //验证amount是合法数字且大于0且小数点后无法超过2位
  if(isNaN(form.value.subscriptionAmount) || form.value.subscriptionAmount <= 0 || form.value.subscriptionAmount.toString().split('.')[1]?.length > 2){
    return ElMessage.warning('请输入正确的金额');
  }
  const data: SubscriptionDTO = {
    productName: form.value.productName,
    productId: form.value.productId,
    tradingAccountId: form.value.tradingAccountId,
    amount: form.value.subscriptionAmount,
    fundAccount: userInfo.fundAccount
  };
  try{
    const res = await submitSubscriptionService(data);
    transactionId.value = res.data;
    dialogVisible.value = true;
  }catch{
    resetForm();
  }
};
const getTradingAccounts = async () => {
  if(userInfo.fundAccount === ''){
    router.push('/login');
    return ElMessage.warning('请先登录');
  }
  const res = await getTradingAccountsService(userInfo.fundAccount);
  tradingAccounts.value = res.data;
};
const resetForm = () => {
  form.value.tradingAccountId = '';
  form.value.subscriptionAmount = 0;
};
getProductInfo();
getTradingAccounts();

const handleDialogClose = () => {
  dialogVisible.value = false;
  router.push('/transactions');
};
</script>

<template>
  <div class="subscription-container">

    <div style="display: flex; align-items: center; justify-content: center;margin-left: -300px">
      <img src="@/assets/logo.png" alt="WiseInvest" class="logo-img" style="width: 120px; height: 60px; margin-right: 10px;">
      <h3 style="padding-top: 0; padding-bottom: 0; margin: 0; color: #0b407ce0; font-size: 1.25em;">基金申购页面</h3>
    </div>
    <el-form :model="form" label-width="150px" class="form-container">
      <!-- 产品名 -->
      <el-form-item label="产品名">
        <div>{{ form.productName }}</div>
      </el-form-item>
      <!-- 基金账户 -->
      <el-form-item label="交易账户">
        <el-select v-model="form.tradingAccountId" placeholder="请选择交易账户">
          <el-option v-for="account in tradingAccounts" :key="account" :value="account" />
        </el-select>
      </el-form-item>
      <!-- 申购金额 -->
      <el-form-item label="申购金额">
        <el-input v-model="form.subscriptionAmount" placeholder="请输入申购金额">
          <template #append><span>元</span></template>
        </el-input>
      </el-form-item>
      <!-- 操作按钮 -->
      <el-form-item>
        <el-button type="primary" @click="submitSubscription" style="margin-right: 30px;">提交</el-button>
        <el-button type="warning" @click="resetForm" style="margin-right: 30px;">重置</el-button>
        <el-button type="default" @click="router.push('/product');" style="margin-right: 30px;">返回</el-button>
      </el-form-item>
      <el-dialog v-model="dialogVisible" title="申购成功" @close="handleDialogClose">
        <div>
          <p style="color: #67C23A; font-weight: bold;">申购已成功提交！</p>
          <p>交易 ID: {{ transactionId }}</p>
          <p>产品名称: {{ form.productName }}</p>
          <p>交易账户: {{ form.tradingAccountId }}</p>
          <p>交易金额: {{ form.subscriptionAmount }} 元</p>
        </div>
        <div style="text-align: right;">
          <el-button type="primary" @click="handleDialogClose">确定</el-button>
        </div>
      </el-dialog>
    </el-form>
  </div>
</template>

<style scoped>
.subscription-container {
  padding: 2px;
  background-color: #cad1d82e;
  min-height: 86.4vh;
  display: flex;
  flex-direction: column;
  align-items: center; /* 使子元素水平居中 */
  gap: 60px; /* 新增：设置子元素垂直间距 */
  padding: 20px 0; /* 可选：增加容器内边距 */
}

.form-container {
  margin-left: 40px;
  width: 800px;
}

.el-button {
  margin-right: 10px;
}
</style>

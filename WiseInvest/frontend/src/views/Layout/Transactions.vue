<script lang="ts" setup>
import { ref,computed } from 'vue';
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage, ElMessageBox } from 'element-plus';
import { useUserInfoStore } from '@/stores/userInfo';
import type { Transaction } from '@/types/query';
import { getTransactionsService } from '@/api/query';
import { cancelService } from '@/api/transaction';
import { useRouter } from 'vue-router';

const userInfoStore = useUserInfoStore();
const userInfo = userInfoStore.info;
const Transactions = ref<Transaction[]>([]);
const router = useRouter();
const getTransactions = async () => {
  if(userInfo.fundAccount === ''){
    router.push('/login');
    return ElMessage.warning('请先登录');
  }
  const res = await getTransactionsService(userInfo.fundAccount);
  Transactions.value = res.data;
  console.log(Transactions.value);
};
getTransactions();
const state =  (isCancel: boolean,canCancel: boolean): string => {
  if(isCancel)
    return '已撤单';
  else if(canCancel)
    return '待确认';
  else
    return '已确认';
}
const cancel = async (transactionId: string) => {
  await cancelService(transactionId);
  Transactions.value = Transactions.value.map(transaction => {
    if(transaction.transactionId === transactionId){
      transaction.cancel = true;
      transaction.canCancel = false;
    }
    return transaction;
  });
  return ElMessage.success('撤单成功');
};
</script>

<template>
  <el-card style="width: 99.9%; height: 99.8%;">
    <div style="display: flex; align-items: center; justify-content: center;">
      <img src="@/assets/logo.png" alt="WiseInvest" class="logo-img" style="width: 120px; height: 60px; margin-right: 10px;">
      <h3 style="padding-top: 0; padding-bottom: 0; margin: 0; color: #0b407ce0; font-size: 1.25em;">产品交易记录页面</h3>

    </div>
    <el-table :data="Transactions" style="width: 100%;">
      <el-table-column prop="transactionId" label="交易 ID" align="center"></el-table-column>
      <el-table-column prop="productName" label="产品名称" align="center"></el-table-column>
      <el-table-column label="金额/份额" align="center">
        <template #default="{ row }">
          <span>{{ row.subscribe ? row.amount : row.shares }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="applicationTime" label="申请时间" align="center"></el-table-column>
      <el-table-column label="交易类型" align="center">
        <template #default="{ row }">
          <span>{{ row.subscribe ? '申购' : '赎回' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center">
        <template #default="{ row }">
          <span>{{ state(row.cancel, row.canCancel) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="canCancel" label="操作" align="center">
        <template #default="{ row }">
          <el-button type="danger" @click="cancel(row.transactionId);" v-if="row.canCancel">撤单</el-button>
          <span v-else>不可操作</span>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<style scoped>
</style>

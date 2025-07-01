<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage, ElForm, ElFormItem, ElInput, ElButton, ElCard, ElTable, ElTableColumn, ElPagination } from 'element-plus';
import { getCustomersByPageService, getCustomerTotalService, getBankcardsService } from '@/api/account';
import type { BankcardVO,CustomerInfo } from '@/types/account';

// 定义响应式的数据
const form = ref({
  pageNum: 1,
  pageSize: 10,
  key: "", // 搜索关键字
});

const customers = ref<CustomerInfo[]>([]); // 用于存储客户数据
const total = ref(0); // 总数据条数

// 获取客户数据的函数
const getCustomersByPage = async () => {
  const res = await getCustomersByPageService(form.value.pageNum, form.value.pageSize, key.value);
  console.log(res.data);
  customers.value = res.data; // 存储客户数据
};

const key = ref("");

// 监听分页或关键字变化，重新加载数据
const handleSearch = async () => {
  form.value.pageNum = 1; // 搜索时重置页码
  key.value = form.value.key;
  await getCustomersByPage();
};

const handlePageChange = async () => {
  await getCustomersByPage();
};

const handlePageSizeChange = async () => {
  form.value.pageNum = 1;
  await getCustomersByPage();
};

const getCustomerTotal = async () => {
  const res = await getCustomerTotalService();
  total.value = res.data; // 存储客户数据
};

const bankcards = ref<BankcardVO[]>([]);
const dialogVisible = ref(false);
const openCustomerDetail = async (fundAccount: string) => {
  dialogVisible.value = true;
  let res = await getBankcardsService(fundAccount);
  bankcards.value = res.data;
};

// 初始加载数据
getCustomersByPage();
getCustomerTotal();
</script>

<template>
  <el-card style="width: 99.9%; height: 99.8%;">
    <el-form :inline="true" style="margin-bottom: 20px; display: flex; justify-content: flex-end;">
      <el-form-item label="搜索关键字" style="margin-right: 10px;width: 400px;">
        <el-input v-model="form.key" placeholder="请输入客户姓名"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="customers">
      <el-table-column prop="name" label="姓名" align="center"></el-table-column>
      <el-table-column prop="phoneNumber" label="手机号" align="center"></el-table-column>
      <el-table-column prop="fundAccount" label="基金账号" align="center"></el-table-column>
      <el-table-column prop="riskLevel" label="风险等级" align="center"></el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="{ row }">
          <el-button type="text" size="small" @click="openCustomerDetail(row.fundAccount)">查看银行卡</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="客户银行卡" width="50%" v-model="dialogVisible" @close="dialogVisible = false">
      <el-table :data="bankcards">
        <el-table-column prop="tradingAccount" label="交易账号" align="center"></el-table-column>
        <el-table-column prop="bankcardNumber" label="银行卡号" align="center"></el-table-column>
      </el-table>
    </el-dialog>
    <!-- 分页器居中，并支持选择每页显示条数 -->
    <div style="display: flex; justify-content: center; margin-top: 20px;">
      <el-pagination @current-change="handlePageChange" @size-change="handlePageSizeChange" :current-page="form.pageNum"
                     :page-size="form.pageSize"  background layout="total, sizes, prev, pager, next" :total="total" />
    </div>
  </el-card>
</template>

<style scoped>
</style>

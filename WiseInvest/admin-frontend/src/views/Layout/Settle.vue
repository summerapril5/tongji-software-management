<script lang="ts" setup>
/**
 * 模拟清算流程页面脚本
 *
 * @description 实现清算日流程操作，包括日初始化、接收行情数据、确认申购赎回、停止申请、导出数据等。
 * 页面包含时间轴组件，结合系统状态动态控制按钮行为与时间轴状态。
 *
 * @author 闫雯晴
 * @since 2025-05-25
 */

import { onMounted, ref } from 'vue'
import {
  initializeDayService,
  receiveMarketDataService,
  confirmSubscriptionDataService,
  confirmRedemptionDataService,
  stopDailyApplicationsService,
  exportDataService,
  getSystemService
} from '@/api/settle' // 导入 API 服务函数
import { ElMessage } from 'element-plus'
import { MoreFilled } from '@element-plus/icons-vue'

/**
 * 初始化交易日流程
 * @returns Promise<void>
 */
const initializeDay = async () => {
  try {
    await initializeDayService()
    ElMessage.success('日初始化成功')
  } catch (error) {
    ElMessage.error('日初始化失败')
  }
}

/**
 * 接收行情数据
 * @returns Promise<void>
 */
const receiveMarketData = async () => {
  try {
    await receiveMarketDataService()
    ElMessage.success('接收行情数据成功')
  } catch (error) {
    ElMessage.error('接收行情数据失败')
  }
}

/**
 * 确认申购数据
 * @returns Promise<void>
 */
const confirmSubscriptionData = async () => {
  try {
    await confirmSubscriptionDataService()
    ElMessage.success('确认申购数据成功')
  } catch (error) {
    ElMessage.error('确认申购数据失败')
  }
};

/**
 * 确认赎回数据
 * @returns Promise<void>
 */
const confirmRedemptionData = async () => {
  try {
    await confirmRedemptionDataService()
    ElMessage.success('确认赎回数据成功')
  } catch (error) {
    ElMessage.error('确认赎回数据失败')
  }
};

/**
 * 停止当日申请
 * @returns Promise<void>
 */
const stopDailyApplications = async () => {
  try {
    await stopDailyApplicationsService()
    ElMessage.success('停止申请成功')
  } catch (error) {
    ElMessage.error('停止申请失败')
  }
};

/**
 * 导出当日数据
 * @returns Promise<void>
 */
const exportData = async () => {
  try {
    await exportDataService()
    ElMessage.success('导出数据成功')
  } catch (error) {
    ElMessage.error('导出数据失败')
  }
};

// 定义时间轴数据（五个阶段的操作节点）
const timelineData = ref([
  {
    content: '接收行情数据',
    timestamp: '',
    color: '#409EFF',
    action: receiveMarketData,
  },
  {
    content: '确认申购请求',
    timestamp: '',
    color: '#409EFF',
    action: confirmSubscriptionData,
  },
  {
    content: '确认赎回请求',
    timestamp: '',
    color: '#409EFF',
    action: confirmRedemptionData,
  },
  {
    content: '停止当日申请',
    timestamp: '',
    color: '#409EFF',
    action: stopDailyApplications,
  },
  {
    content: '导出数据',
    timestamp: '',
    color: '#409EFF',
    action: exportData,
  }
])

const ourSystem = ref(); // 当前系统状态数据
const action = ref();     // 当前操作状态（按钮文案）

/**
 * 页面主按钮点击事件，根据当前流程阶段执行相应动作
 */
const click = async () => {
  if (action.value === "日初始化") {
    await initializeDay();
    // 重置时间轴颜色
    timelineData.value[0].color = '#409EFF';
    timelineData.value[1].color = '#409EFF';
    timelineData.value[2].color = '#409EFF';
    timelineData.value[3].color = '#409EFF';
    timelineData.value[4].color = '#409EFF';
    action.value = "接受行情";

    const res = await getSystemService();
    ourSystem.value = res.data;
    const originalDate = new Date(ourSystem.value.transactionDate);
    originalDate.setHours(originalDate.getHours() + 8); // 转为本地时间
    ourSystem.value.transactionDate = originalDate.toLocaleDateString('en-CA'); // 格式 YYYY-MM-DD

  } else if (action.value === "接受行情") {
    await receiveMarketData();
    timelineData.value[0].color = '#0bbd87'; // 绿色表示完成
    action.value = "申购清算";

  } else if (action.value === "申购清算") {
    await confirmSubscriptionData();
    timelineData.value[1].color = '#0bbd87';
    action.value = "赎回清算";

  } else if (action.value === "赎回清算") {
    await confirmRedemptionData();
    timelineData.value[2].color = '#0bbd87';
    action.value = "停止申请";

  } else if (action.value === "停止申请") {
    await stopDailyApplications();
    timelineData.value[3].color = '#0bbd87';
    action.value = "导出数据";

  } else if (action.value === "导出数据") {
    await exportData();
    timelineData.value[4].color = '#0bbd87';
    action.value = "日初始化";

  } else {
    ElMessage.error('操作失败');
  }
}

/**
 * 获取系统状态并根据状态判断下一步操作与时间轴高亮
 */
const getSystem = async () => {
  const res = await getSystemService();
  ourSystem.value = res.data;
  console.log(ourSystem.value);

  const originalDate = new Date(ourSystem.value.transactionDate);
  originalDate.setHours(originalDate.getHours() + 8);
  ourSystem.value.transactionDate = originalDate.toLocaleDateString('en-CA');

  if (!ourSystem.value.hasReceivedMarketData) {
    action.value = "接受行情"
  } else if (!ourSystem.value.hasConfirmedSubscription) {
    action.value = "申购清算"
    timelineData.value[0].color = '#0bbd87';
  } else if (!ourSystem.value.hasConfirmedRedemption) {
    action.value = "赎回清算"
    timelineData.value[0].color = '#0bbd87';
    timelineData.value[1].color = '#0bbd87';
  } else if (!ourSystem.value.hasStoppedApplication) {
    action.value = "停止申请"
    timelineData.value[0].color = '#0bbd87';
    timelineData.value[1].color = '#0bbd87';
    timelineData.value[2].color = '#0bbd87';
  } else if (!ourSystem.value.hasExportedApplicationData) {
    action.value = "导出数据"
    timelineData.value[0].color = '#0bbd87';
    timelineData.value[1].color = '#0bbd87';
    timelineData.value[2].color = '#0bbd87';
    timelineData.value[3].color = '#0bbd87';
  } else {
    action.value = "日初始化"
    timelineData.value[0].color = '#0bbd87';
    timelineData.value[1].color = '#0bbd87';
    timelineData.value[2].color = '#0bbd87';
    timelineData.value[3].color = '#0bbd87';
    timelineData.value[4].color = '#0bbd87';
  }
}

// 初始化系统状态（页面加载时调用）
getSystem();

</script>

<template>
  <div class="timeline-container">
    <!-- Title -->
    <h2 class="timeline-title">模拟清算</h2>
    <h2 class="timeline-title">当前日期：{{ ourSystem.transactionDate }}</h2>
    <div style="display: flex; justify-content: flex-end; margin-block: 50px; margin-right: 150px; ;">
      <el-button :type="'primary'" size="large" @click="click">{{ action }}</el-button>
    </div>
    <!-- Timeline -->
    <el-timeline>
      <el-timeline-item
          v-for="(activity, index) in timelineData"
          :key="index"
          :color="activity.color"
          :timestamp="activity.timestamp"
      >
        {{ activity.content }}
      </el-timeline-item>
    </el-timeline>
  </div>
</template>

<style scoped>
.timeline-container {
  text-align: center; /* Center the entire container */
  padding: 20px;
}

.timeline-title {
  font-size: 24px; /* Bigger font size for the title */
  font-weight: bold;
  margin-bottom: 50px; /* Space between the title and the timeline */
  color: #0b407ce0;
}

.el-timeline {
  display: inline-block;
  text-align: left; /* Keep the timeline items aligned to the left */
  max-width: 1000px; /* Max width for the timeline */
  margin: 0 auto; /* Center the timeline itself */
}
</style>


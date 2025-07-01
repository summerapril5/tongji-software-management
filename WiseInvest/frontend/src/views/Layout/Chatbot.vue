<!-- src/views/Assistant.vue -->
<template>
  <div class="assistant-container">
    <!-- 聊天区域 -->
    <div class="chat-container">
      <div class="chat-header">
        <div class="avatar">
          <i class="iconfont icon-robot"></i>
        </div>
        <div class="chat-title">智慧客服小V</div>
      </div>

      <div class="chat-messages">
        <div class="message bot-message">
          <div>{{ greeting }}~请问有什么我可以帮到您的？</div>
          <div class="message-time">{{ currentTime }}</div>
        </div>

        <div v-for="(msg, index) in messages" :key="index"
             :class="['message', msg.sender === 'user' ? 'user-message' : 'bot-message']">
          <div>{{ msg.content }}</div>
          <div class="message-time">{{ msg.time }}</div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
            v-model="newMessage"
            placeholder="请告诉我您的需求，例如：如何购买基金产品"
            @keyup.enter="sendMessage"
        ></el-input>
        <el-button type="primary" @click="sendMessage" class="send-button">
          <i class="iconfont icon-send"></i> 发送
        </el-button>
      </div>
    </div>

    <!-- 帮助信息区域 -->
    <div class="help-container">
      <div class="help-card">
        <div class="card-title">
          <i class="iconfont icon-question"></i>
          <span>常见问题帮助</span>
        </div>
        <div class="help-content">
          您好，如果您还没有绑定银行卡，首先您需要在个人信息页面下方绑卡。然后您可以到产品列表页面搜索您想购买的基金产品，我们也为您推荐了您可能感兴趣的产品。
        </div>

        <div class="help-steps">
          <div class="step">
            <div class="step-number">1</div>
            <div class="step-content">在"账号信息"页面绑定您的银行卡</div>
          </div>
          <div class="step">
            <div class="step-number">2</div>
            <div class="step-content">前往"产品列表"浏览基金产品</div>
          </div>
          <div class="step">
            <div class="step-number">3</div>
            <div class="step-content">选择您感兴趣的基金产品并查看详情</div>
          </div>
          <div class="step">
            <div class="step-number">4</div>
            <div class="step-content">点击"购买"按钮完成交易</div>
          </div>
        </div>

        <div class="tip-box">
          <i class="iconfont icon-tip"></i> 提示：您可以点击下方的快捷链接直接前往相关页面
        </div>

        <div class="help-links">
          <el-button class="help-link" @click="goToPage('info')">
            <i class="iconfont icon-user"></i>
            <div class="link-title">账号信息</div>
          </el-button>
          <el-button class="help-link" @click="goToPage('product')">
            <i class="iconfont icon-list"></i>
            <div class="link-title">产品列表</div>
          </el-button>
          <el-button class="help-link" @click="goToPage('transactions')">
            <i class="iconfont icon-transaction"></i>
            <div class="link-title">交易记录</div>
          </el-button>
        </div>
      </div>

      <div class="help-card">
        <div class="card-title">
          <i class="iconfont icon-service"></i>
          <span>客服支持</span>
        </div>
        <div class="help-content">
          <p>我们的智慧客服小V可以为您解答大部分常见问题。如果小V无法解决您的问题，您可以选择以下方式联系人工客服：</p>

          <div class="help-steps">
            <div class="step">
              <div class="step-number"><i class="iconfont icon-phone"></i></div>
              <div class="step-content">
                <strong>客服热线：</strong> 18332639985（工作日 9:00-18:00）
              </div>
            </div>
            <div class="step">
              <div class="step-number"><i class="iconfont icon-email"></i></div>
              <div class="step-content">
                <strong>邮箱支持：</strong> 2253206@tongji.edu.cn
              </div>
            </div>
            <div class="step">
              <div class="step-number"><i class="iconfont icon-chat"></i></div>
              <div class="step-content">
                <strong>在线客服：</strong> 可以通过客服热线联系在线客服
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="service-info">
        <div class="service-title">智慧客服</div>
        <div class="service-desc">24小时智能服务，随时为您解答问题</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()


// 聊天消息
const messages = ref([
  {

  }
])

// 新消息
const newMessage = ref('')

// 当前时间
const currentTime = ref('')

// 问候语
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const responses = ref([
  '选择合适的基金产品需要考虑多个因素，包括您的投资目标、风险承受能力、投资期限等。建议您根据自己的财务状况和投资偏好，选择适合自己的基金类型，如股票基金、债券基金、混合基金等。您也可以咨询专业的理财顾问或查看基金的历史业绩和评级。',
  '您好！购买基金产品前，请确保您已经绑定了银行卡。然后您可以前往产品列表页面，搜索您感兴趣的基金产品，查看产品详情后点击“购买”按钮完成交易。如果您需要更详细的步骤，可以查看帮助中心的指南。',
  '基金的收益通常通过净值增长来计算。基金净值是指基金总资产减去总负债后的余额，除以基金份额总数。基金收益 = （赎回时的净值 - 申购时的净值）× 持有份额。例如，您以1.2元的净值申购了1000份基金，赎回时净值为1.5元，那么您的收益为（1.5 - 1.2）× 1000 = 300元。',
  '为了更好的帮助您，您可以描述更具体的问题吗？例如您想了解哪方面的基金产品信息？',
  '已为您记录此问题，稍后会有专员联系您提供进一步帮助。'
]);
// 当前回答的索引
const responseIndex = ref(0);

// 发送消息
const sendMessage = () => {
  if (newMessage.value.trim() === '') return

  // 添加用户消息
  const now = new Date()
  const timeStr = now.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})

  messages.value.push({
    sender: 'user',
    content: newMessage.value,
    time: timeStr
  });
  messages.value.push({
    sender: 'bot',
    content: '思考中...',
    time: timeStr,
  });

  // 滚动到底部
  scrollToBottom();


  // 模拟机器人回复
  setTimeout(() => {
    // 移除“思考中”消息
    messages.value.pop();

    // 随机选择一个回答
    // 获取当前回答
    const currentResponse = responses.value[responseIndex.value];


    messages.value.push({
      sender: 'bot',
      content: currentResponse,
      time: new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})
    })
    // 更新回答索引，循环回答
    responseIndex.value = (responseIndex.value + 1) % responses.value.length;

    // 滚动到底部
    scrollToBottom()
  }, 3000)

  // 清空输入框
  newMessage.value = ''
}

// 导航到其他页面
const goToPage = (page) => {
  router.push(`/${page}`)
}

// 打开在线客服
const openLiveChat = () => {
  alert('正在为您连接在线客服...')
}

// 更新时间
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})
}

// 滚动到聊天底部
const scrollToBottom = () => {
  const chatMessages = document.querySelector('.chat-messages')
  if (chatMessages) {
    chatMessages.scrollTop = chatMessages.scrollHeight
  }
}

onMounted(() => {
  // 初始化时间
  updateTime()
  setInterval(updateTime, 60000)

  // 初始化时滚动到底部
  setTimeout(scrollToBottom, 300)
})
</script>

<style scoped>
.assistant-container {
  display: flex;
  height: calc(100vh - 140px);
  padding: 20px;
  gap: 20px;
  margin: 0 auto; /* 水平居中 */
}

.chat-container {
  flex: 3;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.chat-header {
  background: linear-gradient(90deg, #2E5077 0%, #4DA1A9 100%);
  color: white;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.chat-title {
  font-size: 18px;
  font-weight: bold;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background-color: #f8f9fa;
}

.message {
  max-width: 80%;
  padding: 12px 16px;
  border-radius: 18px;
  position: relative;
  line-height: 1.5;
}

.bot-message {
  background-color: #e3f2fd;
  align-self: flex-start;
  border-bottom-left-radius: 4px;
}

.user-message {
  background-color: #d1e7ff;
  align-self: flex-end;
  border-bottom-right-radius: 4px;
}

.message-time {
  font-size: 12px;
  color: #888;
  margin-top: 4px;
  text-align: right;
}

.chat-input {
  padding: 16px;
  background-color: white;
  border-top: 1px solid #eee;
  display: flex;
  gap: 12px;
}

.send-button {
  height: 40px;
  width: 100px;
  background: #2E5077;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s;
}

.send-button:hover {
  background: #4DA1A9;
}

/* 帮助信息区域 */
.help-container {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.help-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  color: #2E5077;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-title i {
  color: #4DA1A9;
}

.help-content {
  line-height: 1.6;
  color: #555;
}

.help-steps {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
}

.step {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.step-number {
  background: #4DA1A9;
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
}

.tip-box {
  background: #e3f2fd;
  border-left: 4px solid #4DA1A9;
  padding: 12px 16px;
  margin-top: 20px;
  border-radius: 0 8px 8px 0;
}

.help-links {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 20px;
}

.help-link {
  flex: 1;
  min-width: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  background: #f0f7ff;
  border: 1px solid #d1e7ff;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.help-link:hover {
  background: #d1e7ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.help-link i {
  font-size: 24px;
  color: #2E5077;
  margin-bottom: 8px;
}

.link-title {
  font-weight: 600;
  color: #2E5077;
}

.service-info {
  background: linear-gradient(135deg, #2E5077 0%, #4DA1A9 100%);
  color: white;
  text-align: center;
  padding: 16px;
  border-radius: 12px;
}

.service-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 8px;
}

.service-desc {
  font-size: 14px;
  opacity: 0.9;
}
</style>

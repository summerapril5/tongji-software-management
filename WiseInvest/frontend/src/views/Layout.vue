<script setup>
// 引入 vue 的响应式引用工具
import { ref } from 'vue'

// 引入 Element Plus 的布局和导航组件
import { ElContainer, ElAside, ElMenu, ElMenuItem, ElHeader, ElMain, ElFooter } from 'element-plus';

// 引入 Pinia 中的用户信息 store
import { useUserInfoStore } from '@/stores/userInfo';

// 引入路由对象
import router from '@/router';

// 获取用户信息 store 实例
const userInfoStore = useUserInfoStore();

// 读取当前用户信息对象
const userInfo = userInfoStore.info;

// 将用户信息对象拷贝到本地响应式表单变量中，用于页面展示
const form = ref({ ...userInfo });
</script>

<template>
  <el-container>
    <!-- 左侧菜单区域 -->
    <el-aside width="250px">
      <el-menu active-text-color="#F3F3E0" background-color="#2E5077" text-color="#fff" router>
        <!-- 菜单标题 -->
        <div class="menu-title">⭐系统服务选项⭐</div>
        <!-- 菜单项：账号信息 -->
        <el-menu-item index="/info">
          <span>账号信息</span>
        </el-menu-item>
        <!-- 菜单项：产品列表 -->
        <el-menu-item index="/product">
          <span>产品列表</span>
        </el-menu-item>
        <!-- 菜单项：交易记录 -->
        <el-menu-item index="/transactions">
          <span>交易记录</span>
        </el-menu-item>
        <el-menu-item index="/chatbot">
          <span>智慧客服</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧主内容区域 -->
    <el-container>

      <!-- 顶部区域：包含欢迎语与用户信息 -->
      <el-header class="main-header">
        <div class="header-container">
          <!-- Logo 图片 -->
          <img src="@/assets/logo.png" alt="Logo" class="header-logo">
          <!-- 欢迎语 + 用户名 + 基金账号 -->
          <div>
            欢迎 {{ form.name }} 进入系统 基金帐号：{{ form.fundAccount }}
          </div>
        </div>
      </el-header>

      <!-- 主体内容区域：动态渲染子路由页面 -->
      <el-main>
        <router-view />
      </el-main>

      <!-- 底部区域 -->

    </el-container>
  </el-container>
</template>

<style scoped>
/* 页面总容器，高度撑满视口，横向布局 */
.el-container {
  height: 100vh;
  display: flex;
}

/* 左侧菜单栏样式 */
.el-aside {
  background-color: #2E5077;
  color: #2E5077;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

/* 菜单组件样式优化：去掉边框 */
.el-menu {
  border-right: none;
}

/* 菜单项样式调整 */
.el-menu-item {
  transition: background-color 0.3s ease;
  font-size: 16px;
}

/* 菜单项 hover 和激活状态的背景色 */
.el-menu-item:hover, .el-menu-item.is-active {
  background-color: #4DA1A9;
}

/* 头部与底部背景色与字体颜色设置 */
.el-header, .el-footer {
  background-color: #f5f7fa;
  color: #606266;
  text-align: center;
  line-height: 60px;
}

/* 主体区域样式 */
.el-main {
  padding: 0px;
  background-color: #fff;
  overflow: auto;
}

/* 菜单标题样式 */
.menu-title {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50px;
  padding: 10px;
  font-size: 18px;
  color: #f6bc00;
  background-color: #2E5077;
}

/* 顶部 header 样式：内容居中、背景美化 */
.main-header {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80px !important;
  background: #f8f9fa;
}

/* 顶部容器内容排布 */
.header-container {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

/* Logo 图片样式控制 */
.header-logo {
  width: 60px;
  height: 60px;
  object-fit: contain;
}
</style>

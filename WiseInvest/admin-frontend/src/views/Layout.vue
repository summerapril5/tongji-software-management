<script setup>
import { ref } from 'vue'
import { ElContainer, ElAside, ElMenu, ElMenuItem, ElHeader, ElMain, ElFooter } from 'element-plus';
import { useTokenStore } from '@/stores/token';
import { useUserInfoStore } from '@/stores/userInfo';
import { useRouter } from 'vue-router';

const tokenStore = useTokenStore();
const userInfoStore = useUserInfoStore();
const userInfo = userInfoStore.info;
const router = useRouter();
if (!userInfoStore.info.adminAccount) {
  router.push('/login');
}
const form = ref({ ...userInfo });
const logout = () => {
  tokenStore.removeToken();
  userInfoStore.removeInfo();
  router.push('/login');
};
</script>

<template>
  <el-container>
    <!-- 左侧菜单 -->
    <el-aside width="250px">
      <el-menu active-text-color="#F3F3E0" background-color="#2E5077" text-color="#fff" router>
        <div class="menu-title">管理员系统</div>
        <el-menu-item index="/info">
          <span>客户信息</span>
        </el-menu-item>
        <el-menu-item index="/product">
          <span>产品列表</span>
        </el-menu-item>
        <el-menu-item index="/settle">
          <span>系统清算</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <!-- 右侧主区域 -->
    <el-container>
      <!-- 头部区域 -->
      <el-header >
        <div>
          管理员帐号：{{ form.adminAccount }}
          <el-button type="danger" @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <!-- 中间区域 -->
      <el-main>
        <router-view />
      </el-main>
      <!-- 底部区域 -->
      <el-footer>同济基金交易 管理员系统</el-footer>
    </el-container>
  </el-container>
</template>

<style scoped>
/* 容器 */
.el-container {
  height: 100vh;
  display: flex;
}

/* 侧边栏 */
.el-aside {
  background-color: #2E5077;
  color: #2E5077;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.el-menu {
  border-right: none; /* 移除默认右侧边框 */
}

.el-menu-item {
  transition: background-color 0.3s ease;
  font-size: 16px;
}

.el-menu-item:hover, .el-menu-item.is-active {
  background-color: #4DA1A9;
}

.el-header, .el-footer {
  background-color: #f5f7fa;
  color: #606266;
  text-align: center;
  line-height: 60px;
}

.el-main {
  padding: 0px;
  background-color: #fff;
  overflow: auto;
}

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
</style>

//导入vue-router
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import LoginVue from '@/views/Login.vue';
import ResetPwVue from '@/views/Resetpw.vue';
import UserInfoVue from '@/views/Layout/UserInfo.vue';
import LayoutVue from '@/views/Layout.vue';
import ProductVue from '@/views/Layout/Product.vue';
import SettleVue from '@/views/Layout/Settle.vue';
//定义路由关系
const routes:RouteRecordRaw[] = [
    { path: '/login', component: LoginVue },
    { path: '/resetpw', component: ResetPwVue },
    { path: '/', component: LayoutVue, redirect:'/login',children:[
        { path: '/info', component: UserInfoVue },
        { path: '/product', component: ProductVue },
        { path: '/settle', component: SettleVue }
        ]},
    { path: '/', redirect: '/login' }
]
//创建路由器
const router = createRouter({
    history: createWebHistory(),
    routes: routes
});
export default router

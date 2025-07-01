//导入vue-router
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import LoginVue from '@/views/Login.vue'
import ResetPwVue from '@/views/Resetpw.vue'
import CreateAccountVue from '@/views/CreateAccount.vue';
import UserInfoVue from '@/views/Layout/UserInfo.vue';
import LayoutVue from '@/views/Layout.vue';
import ProductVue from '@/views/Layout/Product.vue';
import SubscriptionVue from '@/views/Layout/Subscription.vue';
import RedemptionVue from '@/views/Layout/Redemption.vue';
import TransactionsVue from '@/views/Layout/Transactions.vue';
import ChatbotVue from '@/views/Layout/Chatbot.vue';



//定义路由关系
const routes:RouteRecordRaw[] = [
    { path: '/login', component: LoginVue },
    { path: '/resetpw', component: ResetPwVue },
    { path: '/create', component: CreateAccountVue },
    { path: '/', component: LayoutVue, redirect:'/login',children:[
            { path: '/info', component: UserInfoVue },
            { path: '/product', component: ProductVue },
            { path: '/subscription', component: SubscriptionVue },
            { path: '/redemption', component: RedemptionVue},
            { path: '/transactions', component: TransactionsVue },
            { path: '/chatbot', component:ChatbotVue}
        ]},
    { path: '/', redirect: '/login' },
]
//创建路由器
const router = createRouter({
    history: createWebHistory(),
    routes: routes
});
export default router

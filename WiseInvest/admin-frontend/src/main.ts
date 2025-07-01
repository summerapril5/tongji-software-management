import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import './assets/main.css'
import router from '@/router'
import { createPinia } from 'pinia'
import { createPersistedState } from 'pinia-plugin-persistedstate'

import App from './App.vue'

const app = createApp(App)
const pinia = createPinia()
const persist = createPersistedState()

app.use(ElementPlus, { locale: zhCn })
pinia.use(persist)
app.use(pinia)
app.use(router)
app.mount('#app')

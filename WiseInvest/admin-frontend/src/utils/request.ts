import axios from 'axios';
import { ElMessage } from 'element-plus'
import { useTokenStore } from '@/stores/token.js';

const baseURL = 'http://localhost:8082';
const instance = axios.create({baseURL})

//添加响应拦截器
instance.interceptors.response.use(
    result=>{
        if(result.data.code == 0){
            return result.data;
        }
        ElMessage.error(result.data.msg || '服务异常');
        return Promise.reject(result.data);
    },
    err=>{
        ElMessage.error('服务异常');
        return Promise.reject(err);//异步的状态转化成失败的状态
    }
)

//添加请求拦截器
instance.interceptors.request.use(
    config=>{
        //在发送请求之前做什么
        let tokenStore = useTokenStore()
        //如果token中有值，在携带
        if(tokenStore.token){
            config.headers.Authorization=tokenStore.token
        }
        return config
    },
    err=>{
        //如果请求错误做什么
        Promise.reject(err)
    }
)

export default instance;
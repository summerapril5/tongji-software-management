import request from '@/utils/request.ts'
import type { LoginDTO } from '@/types/login.d.ts';
import { ElMessage } from 'element-plus';

export function loginService(data: LoginDTO) {
    return request.post('/account/login', data);
}

export function resetPwService(data: LoginDTO) {
    return request.patch('/account/login/change_password', data);
}

export function verificationService(phoneNumber: string) {
    return request.get('/account/login/verification', { params: { phoneNumber } });
}

export function sendCodeService(phoneNumber: string) {
    if(!phoneNumber){
        ElMessage.error('请先输入手机号');
        return;
    }
    if(!/^1\d{10}$/.test(phoneNumber)){
        ElMessage.error('手机号是11位数字');
        return;
    }
    verificationService(phoneNumber);
    setTimeout(() => {
        ElMessage.success('验证码已发送');
    }, 500);
}
import { mount } from '@vue/test-utils'
import Login from '@/views/Login.vue'
import { describe, it, expect, vi } from 'vitest'

// 模拟 vue-router 的 useRouter
const mockPush = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({ push: mockPush }),
}))

describe('Login.vue', () => {
  it('renders login form inputs', () => {
    const wrapper = mount(Login)
    expect(wrapper.find('input[placeholder="请输入您的手机号"]').exists()).toBe(true)
    expect(wrapper.find('input[placeholder="请输入您的密码"]').exists()).toBe(true)
  })

  it('calls login on button click', async () => {
    const wrapper = mount(Login)
    await wrapper.find('button.login-button').trigger('click')
    // 此处可进一步 mock loginService 并断言调用
  })

  it('navigates to /resetpw on forgot password click', async () => {
    const wrapper = mount(Login)
    await wrapper.find('button.forget-button').trigger('click')
    expect(mockPush).toHaveBeenCalledWith('/resetpw')
  })

  it('navigates to /create on register button click', async () => {
    const wrapper = mount(Login)
    await wrapper.find('button.register-button').trigger('click')
    expect(mockPush).toHaveBeenCalledWith('/create')
  })
})

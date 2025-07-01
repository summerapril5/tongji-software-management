import { mount } from '@vue/test-utils'
import CreateAccount from '@/views/CreateAccount.vue'
import { describe, it, expect } from 'vitest'

describe('CreateAccount.vue', () => {
  it('renders all input fields', () => {
    const wrapper = mount(CreateAccount)
    expect(wrapper.find('input[placeholder="请输入姓名"]').exists()).toBe(true)
    expect(wrapper.find('input[placeholder="请输入手机号"]').exists()).toBe(true)
    expect(wrapper.find('input[placeholder="请输入验证码"]').exists()).toBe(true)
    expect(wrapper.find('input[placeholder="请输入身份证号码"]').exists()).toBe(true)
  })

  it('renders and triggers send code button', async () => {
    const wrapper = mount(CreateAccount)
    const sendBtn = wrapper.find('button')
    expect(sendBtn.exists()).toBe(true)
    await sendBtn.trigger('click')
    // 可进一步 mock sendCodeService 的行为验证
  })

  it('shows validation errors on empty submit (manual trigger)', async () => {
    const wrapper = mount(CreateAccount)
    const form = wrapper.findComponent({ ref: 'formRef' })
    expect(form.exists()).toBe(true)
    await form.vm.validate?.()
    // 建议结合 Element Plus 的 Form 错误提示断言
  })
})

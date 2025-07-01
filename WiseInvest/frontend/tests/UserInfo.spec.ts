import { mount } from '@vue/test-utils'
import UserInfo from '@/views/UserInfo.vue'
import { describe, it, expect } from 'vitest'

describe('UserInfo.vue', () => {
  it('displays user info title', () => {
    const wrapper = mount(UserInfo)
    expect(wrapper.text()).toContain('用户信息')
  })
})
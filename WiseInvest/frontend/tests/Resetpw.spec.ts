import { mount } from '@vue/test-utils'
import Resetpw from '@/views/Resetpw.vue'
import { describe, it, expect } from 'vitest'

describe('Resetpw.vue', () => {
  it('renders the reset password form', () => {
    const wrapper = mount(Resetpw)
    expect(wrapper.text()).toContain('找回密码')
  })
})
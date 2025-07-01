import { mount } from '@vue/test-utils'
import Subscription from '@/views/Subscription.vue'
import { describe, it, expect } from 'vitest'

describe('Subscription.vue', () => {
  it('renders subscription table', () => {
    const wrapper = mount(Subscription)
    expect(wrapper.exists()).toBe(true)
  })
})
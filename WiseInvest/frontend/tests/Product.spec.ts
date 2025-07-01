import { mount } from '@vue/test-utils'
import Product from '@/views/Product.vue'
import { describe, it, expect } from 'vitest'

describe('Product.vue', () => {
  it('renders product list', () => {
    const wrapper = mount(Product)
    expect(wrapper.exists()).toBe(true)
  })
})
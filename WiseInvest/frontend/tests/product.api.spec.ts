import { describe, it, expect, vi } from 'vitest'
import * as productApi from '@/api/product'
import axios from 'axios'

vi.mock('axios')
const mockedAxios = axios as jest.Mocked<typeof axios>

describe('product API', () => {
  it('should fetch product list', async () => {
    mockedAxios.get.mockResolvedValueOnce({ data: [{ id: 1, name: '基金A' }] })
    const result = await productApi.fetchProductList()
    expect(result[0].name).toBe('基金A')
  })
})
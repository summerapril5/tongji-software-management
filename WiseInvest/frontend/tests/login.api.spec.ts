import { describe, it, expect, vi } from 'vitest'
import * as loginApi from '@/api/login'
import axios from 'axios'

vi.mock('axios')
const mockedAxios = axios as jest.Mocked<typeof axios>

describe('login API', () => {
  it('should login user', async () => {
    mockedAxios.post.mockResolvedValueOnce({ data: { code: 0, token: 'abc' } })
    const result = await loginApi.login({ username: 'test', password: '1234' })
    expect(result.code).toBe(0)
  })
})
/**
 * @file settle.spec.ts
 * @description Unit Tests for API methods in settle.ts
 */

import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'
import * as settleApi from '../settle'

// 模拟 axios
vi.mock('axios')
const mockedAxios = axios as unknown as {
    get: ReturnType<typeof vi.fn>
    post: ReturnType<typeof vi.fn>
}

beforeEach(() => {
    mockedAxios.get = vi.fn()
    mockedAxios.post = vi.fn()
})

describe('settle.ts API methods', () => {
    it('initializeDayService should post to /settle/init', async () => {
        mockedAxios.post.mockResolvedValue({ data: 'ok' })
        const res = await settleApi.initializeDayService()
        expect(mockedAxios.post).toHaveBeenCalledWith('/settle/init')
        expect(res.data).toBe('ok')
    })

    it('receiveMarketDataService should post to /settle/market', async () => {
        mockedAxios.post.mockResolvedValue({ data: 'ok' })
        const res = await settleApi.receiveMarketDataService()
        expect(mockedAxios.post).toHaveBeenCalledWith('/settle/market')
        expect(res.data).toBe('ok')
    })

    it('confirmSubscriptionDataService should post to /settle/subscription', async () => {
        mockedAxios.post.mockResolvedValue({ data: 'ok' })
        const res = await settleApi.confirmSubscriptionDataService()
        expect(mockedAxios.post).toHaveBeenCalledWith('/settle/subscription')
        expect(res.data).toBe('ok')
    })

    it('confirmRedemptionDataService should post to /settle/redemption', async () => {
        mockedAxios.post.mockResolvedValue({ data: 'ok' })
        const res = await settleApi.confirmRedemptionDataService()
        expect(mockedAxios.post).toHaveBeenCalledWith('/settle/redemption')
        expect(res.data).toBe('ok')
    })

    it('stopDailyApplicationsService should post to /settle/stop', async () => {
        mockedAxios.post.mockResolvedValue({ data: 'ok' })
        const res = await settleApi.stopDailyApplicationsService()
        expect(mockedAxios.post).toHaveBeenCalledWith('/settle/stop')
        expect(res.data).toBe('ok')
    })

    it('exportDataService should post to /settle/export', async () => {
        mockedAxios.post.mockResolvedValue({ data: 'ok' })
        const res = await settleApi.exportDataService()
        expect(mockedAxios.post).toHaveBeenCalledWith('/settle/export')
        expect(res.data).toBe('ok')
    })

    it('getSystemService should get from /settle', async () => {
        mockedAxios.get.mockResolvedValue({ data: { transactionDate: '2025-05-25' } })
        const res = await settleApi.getSystemService()
        expect(mockedAxios.get).toHaveBeenCalledWith('/settle')
        expect(res.data.transactionDate).toBe('2025-05-25')
    })
})

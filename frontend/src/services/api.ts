import type { RouteRequest, RouteWeatherResponse } from '../types'

const BASE_URL = '/api'

export async function getRouteWeather(
  request: RouteRequest
): Promise<RouteWeatherResponse> {
  const response = await fetch(`${BASE_URL}/routes/weather`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  })

  if (!response.ok) {
    const problem = await response.json().catch(() => ({}))
    throw new Error(problem.detail ?? `HTTP ${response.status}`)
  }

  return response.json() as Promise<RouteWeatherResponse>
}

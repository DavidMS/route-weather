import { useState } from 'react'
import { RouteForm } from './components/RouteForm'
import { WeatherReport } from './components/WeatherReport'
import { getRouteWeather } from './services/api'
import type { LoadingState, RouteRequest, RouteWeatherResponse } from './types'

export default function App() {
  const [loadingState, setLoadingState] = useState<LoadingState>('idle')
  const [report, setReport] = useState<RouteWeatherResponse | null>(null)
  const [error, setError] = useState<string | null>(null)

  async function handleRouteSubmit(request: RouteRequest) {
    setLoadingState('loading')
    setError(null)
    setReport(null)

    try {
      const result = await getRouteWeather(request)
      setReport(result)
      setLoadingState('success')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unexpected error')
      setLoadingState('error')
    }
  }

  return (
    <main>
      <h1 className="app-title">🌤️ Route Weather</h1>

      <RouteForm onSubmit={handleRouteSubmit} loading={loadingState === 'loading'} />

      {loadingState === 'loading' && (
        <div className="loading">
          <div className="spinner" />
          Fetching route and forecasts…
        </div>
      )}

      {loadingState === 'error' && error && (
        <div className="error-banner" role="alert">
          {error}
        </div>
      )}

      {loadingState === 'success' && report && (
        <WeatherReport report={report} />
      )}
    </main>
  )
}

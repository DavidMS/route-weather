import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import { WeatherReport } from './WeatherReport'
import type { RouteWeatherResponse } from '../types'

const mockReport: RouteWeatherResponse = {
  origin: 'Madrid',
  destination: 'Barcelona',
  travelDate: '2026-03-01',
  routeGeometry: [
    { latitude: 40.4176, longitude: -3.7037 },
    { latitude: 41.0,    longitude: -1.5    },
    { latitude: 41.385,  longitude: 2.173   },
  ],
  weatherPoints: [
    {
      latitude: 40.4176,
      longitude: -3.7037,
      forecastTime: '2026-03-01T12:00:00',
      temperatureCelsius: 12.5,
      precipitationMm: 0.0,
      windSpeedKmh: 10,
      condition: 'CLEAR',
    },
    {
      latitude: 41.137,
      longitude: -2.438,
      forecastTime: '2026-03-01T12:00:00',
      temperatureCelsius: 9.8,
      precipitationMm: 2.0,
      windSpeedKmh: 20,
      condition: 'RAINY',
    },
    {
      latitude: 41.385,
      longitude: 2.173,
      forecastTime: '2026-03-01T12:00:00',
      temperatureCelsius: 14.2,
      precipitationMm: 0.0,
      windSpeedKmh: 8,
      condition: 'PARTLY_CLOUDY',
    },
  ],
}

describe('WeatherReport', () => {
  it('renders route header with origin and destination', () => {
    render(<WeatherReport report={mockReport} />)

    expect(screen.getByText(/Madrid/)).toBeInTheDocument()
    expect(screen.getByText(/Barcelona/)).toBeInTheDocument()
    expect(screen.getByText('2026-03-01')).toBeInTheDocument()
  })

  it('renders a card for each weather point', () => {
    render(<WeatherReport report={mockReport} />)

    expect(screen.getByText('Start')).toBeInTheDocument()
    expect(screen.getByText('Stop 2')).toBeInTheDocument()
    expect(screen.getByText('End')).toBeInTheDocument()
  })

  it('labels first card as Start and last card as End', () => {
    render(<WeatherReport report={mockReport} />)

    const stops = screen.getAllByText(/^(Start|Stop \d+|End)$/)
    expect(stops[0]).toHaveTextContent('Start')
    expect(stops[stops.length - 1]).toHaveTextContent('End')
  })

  it('displays temperatures for each point', () => {
    render(<WeatherReport report={mockReport} />)

    expect(screen.getByText('12.5°C')).toBeInTheDocument()
    expect(screen.getByText('9.8°C')).toBeInTheDocument()
    expect(screen.getByText('14.2°C')).toBeInTheDocument()
  })

  it('displays condition labels', () => {
    render(<WeatherReport report={mockReport} />)

    expect(screen.getByText('Clear')).toBeInTheDocument()
    expect(screen.getByText('Rainy')).toBeInTheDocument()
    expect(screen.getByText('Partly cloudy')).toBeInTheDocument()
  })

  it('shows fallback message when no weather points', () => {
    render(<WeatherReport report={{ ...mockReport, weatherPoints: [] }} />)

    expect(screen.getByText('No weather data available for this route.')).toBeInTheDocument()
  })
})

import type { RouteWeatherResponse, WeatherPointResponse, WeatherCondition } from '../types'

interface Props {
  report: RouteWeatherResponse
}

const CONDITION_LABEL: Record<WeatherCondition, string> = {
  CLEAR: 'Clear',
  PARTLY_CLOUDY: 'Partly cloudy',
  CLOUDY: 'Cloudy',
  RAINY: 'Rainy',
  HEAVY_RAIN: 'Heavy rain',
  SNOWY: 'Snowy',
  STORMY: 'Stormy',
  FOGGY: 'Foggy',
}

const CONDITION_ICON: Record<WeatherCondition, string> = {
  CLEAR: '☀️',
  PARTLY_CLOUDY: '⛅',
  CLOUDY: '☁️',
  RAINY: '🌧️',
  HEAVY_RAIN: '⛈️',
  SNOWY: '🌨️',
  STORMY: '⛈️',
  FOGGY: '🌫️',
}

function stopLabel(index: number, total: number): string {
  if (index === 0) return 'Start'
  if (index === total - 1) return 'End'
  return `Stop ${index + 1}`
}

function stopClass(index: number, total: number): string {
  if (index === 0) return 'weather-card is-origin'
  if (index === total - 1) return 'weather-card is-destination'
  return 'weather-card is-waypoint'
}

function WeatherCard({
  point,
  index,
  total,
}: {
  point: WeatherPointResponse
  index: number
  total: number
}) {
  return (
    <div className={stopClass(index, total)}>
      <span className="card-stop">{stopLabel(index, total)}</span>
      <div className="card-icon">{CONDITION_ICON[point.condition]}</div>
      <div className="card-condition">{CONDITION_LABEL[point.condition]}</div>
      <div className="card-temp">{point.temperatureCelsius.toFixed(1)}°C</div>
      <div className="card-details">
        <span className="card-detail">💧 {point.precipitationMm} mm</span>
        <span className="card-detail">💨 {point.windSpeedKmh} km/h</span>
      </div>
      <div className="card-coords">
        {point.latitude.toFixed(4)}, {point.longitude.toFixed(4)}
      </div>
    </div>
  )
}

export function WeatherReport({ report }: Props) {
  const total = report.weatherPoints.length

  return (
    <section>
      <div className="route-header">
        <h2>
          {report.origin} → {report.destination}
        </h2>
        <p className="route-date">{report.travelDate}</p>
      </div>

      {total === 0 ? (
        <p>No weather data available for this route.</p>
      ) : (
        <div className="waypoints">
          {report.weatherPoints.map((point, i) => (
            <WeatherCard key={i} point={point} index={i} total={total} />
          ))}
        </div>
      )}
    </section>
  )
}

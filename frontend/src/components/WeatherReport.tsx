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

function WeatherCard({ point }: { point: WeatherPointResponse }) {
  return (
    <div>
      <p>
        <strong>{CONDITION_LABEL[point.condition]}</strong>
      </p>
      <p>
        {point.latitude.toFixed(4)}, {point.longitude.toFixed(4)}
      </p>
      <p>{point.temperatureCelsius.toFixed(1)} °C</p>
      <p>Rain: {point.precipitationMm} mm</p>
      <p>Wind: {point.windSpeedKmh} km/h</p>
    </div>
  )
}

export function WeatherReport({ report }: Props) {
  return (
    <section>
      <h2>
        {report.origin} → {report.destination}
      </h2>
      <p>Travel date: {report.travelDate}</p>

      {report.weatherPoints.length === 0 ? (
        <p>No weather data available for this route.</p>
      ) : (
        <div>
          {report.weatherPoints.map((point, i) => (
            <WeatherCard key={i} point={point} />
          ))}
        </div>
      )}
    </section>
  )
}

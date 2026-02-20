import { useMemo, useEffect } from 'react'
import { MapContainer, TileLayer, Marker, Popup, Polyline, useMap } from 'react-leaflet'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import type { WeatherPointResponse, WeatherCondition } from '../types'

// ─── Icons ────────────────────────────────────────────────────────────────────
// Using divIcon (coloured circles) avoids the classic Leaflet + Vite
// asset-path issue with the default PNG marker icons.

function circleIcon(color: string) {
  return L.divIcon({
    className: '',
    html: `<div style="
      width:14px;height:14px;
      background:${color};
      border:2.5px solid #fff;
      border-radius:50%;
      box-shadow:0 1px 5px rgba(0,0,0,0.4)
    "></div>`,
    iconSize: [14, 14],
    iconAnchor: [7, 7],
    popupAnchor: [0, -10],
  })
}

const ICONS = {
  start:    circleIcon('#48bb78'),   // green  — matches card border
  waypoint: circleIcon('#4299e1'),   // blue
  end:      circleIcon('#ed8936'),   // orange — matches card border
}

// ─── Auto-fit bounds ──────────────────────────────────────────────────────────

function FitBounds({ positions }: { positions: L.LatLngTuple[] }) {
  const map = useMap()
  useEffect(() => {
    if (positions.length > 0) {
      map.fitBounds(L.latLngBounds(positions), { padding: [40, 40], maxZoom: 12 })
    }
  }, [map, positions])
  return null
}

// ─── Labels / emoji (mirrors WeatherReport) ───────────────────────────────────

const CONDITION_ICON: Record<WeatherCondition, string> = {
  CLEAR:        '☀️',
  PARTLY_CLOUDY:'⛅',
  CLOUDY:       '☁️',
  RAINY:        '🌧️',
  HEAVY_RAIN:   '⛈️',
  SNOWY:        '🌨️',
  STORMY:       '⛈️',
  FOGGY:        '🌫️',
}

const CONDITION_LABEL: Record<WeatherCondition, string> = {
  CLEAR:        'Clear',
  PARTLY_CLOUDY:'Partly cloudy',
  CLOUDY:       'Cloudy',
  RAINY:        'Rainy',
  HEAVY_RAIN:   'Heavy rain',
  SNOWY:        'Snowy',
  STORMY:       'Stormy',
  FOGGY:        'Foggy',
}

// ─── Component ────────────────────────────────────────────────────────────────

interface Props {
  weatherPoints: WeatherPointResponse[]
}

export function RouteMap({ weatherPoints }: Props) {
  const positions = useMemo<L.LatLngTuple[]>(
    () => weatherPoints.map((p) => [p.latitude, p.longitude]),
    [weatherPoints],
  )

  const total = weatherPoints.length

  function markerIcon(index: number) {
    if (index === 0)          return ICONS.start
    if (index === total - 1)  return ICONS.end
    return ICONS.waypoint
  }

  function stopLabel(index: number) {
    if (index === 0)          return 'Start'
    if (index === total - 1)  return 'End'
    return `Stop ${index + 1}`
  }

  // Initial center — overridden immediately by FitBounds
  const defaultCenter = positions[0] ?? ([40.0, -4.0] as L.LatLngTuple)

  return (
    <MapContainer center={defaultCenter} zoom={6} className="route-map" scrollWheelZoom={false}>
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />

      <FitBounds positions={positions} />

      {positions.length > 1 && (
        <Polyline
          positions={positions}
          pathOptions={{ color: '#4299e1', weight: 3, opacity: 0.75, dashArray: '6 4' }}
        />
      )}

      {weatherPoints.map((point, i) => (
        <Marker key={i} position={positions[i]} icon={markerIcon(i)}>
          <Popup>
            <div className="map-popup">
              <div className="map-popup-stop">{stopLabel(i)}</div>
              <div className="map-popup-condition">
                {CONDITION_ICON[point.condition]} {CONDITION_LABEL[point.condition]}
              </div>
              <div className="map-popup-temp">{point.temperatureCelsius.toFixed(1)} °C</div>
              <div className="map-popup-detail">💨 {point.windSpeedKmh} km/h</div>
              <div className="map-popup-detail">💧 {point.precipitationMm} mm</div>
            </div>
          </Popup>
        </Marker>
      ))}
    </MapContainer>
  )
}

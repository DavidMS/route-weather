// ─── Request types ────────────────────────────────────────────────────────────

export interface RouteRequest {
  origin: string;
  destination: string;
  travelDate: string; // ISO date string: YYYY-MM-DD
  departureTime: string; // 24-hour time string: HH:mm
}

// ─── Response types ───────────────────────────────────────────────────────────

export interface WeatherPointResponse {
  latitude: number;
  longitude: number;
  forecastTime: string;
  temperatureCelsius: number;
  precipitationMm: number;
  windSpeedKmh: number;
  condition: WeatherCondition;
}

export interface CoordinatesResponse {
  latitude: number;
  longitude: number;
}

export interface RouteWeatherResponse {
  origin: string;
  destination: string;
  travelDate: string;
  weatherPoints: WeatherPointResponse[];
  routeGeometry: CoordinatesResponse[];
}

// ─── Domain enums (mirror the backend) ───────────────────────────────────────

export type WeatherCondition =
  | 'CLEAR'
  | 'PARTLY_CLOUDY'
  | 'CLOUDY'
  | 'RAINY'
  | 'HEAVY_RAIN'
  | 'SNOWY'
  | 'STORMY'
  | 'FOGGY';

// ─── UI state ─────────────────────────────────────────────────────────────────

export type LoadingState = 'idle' | 'loading' | 'success' | 'error';

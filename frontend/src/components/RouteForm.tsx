import { useState, type FormEvent } from 'react'
import type { RouteRequest } from '../types'

interface Props {
  onSubmit: (request: RouteRequest) => void
  loading: boolean
}

export function RouteForm({ onSubmit, loading }: Props) {
  const today = new Date().toISOString().split('T')[0]

  const [origin, setOrigin] = useState('')
  const [destination, setDestination] = useState('')
  const [travelDate, setTravelDate] = useState(today)

  function handleSubmit(e: FormEvent) {
    e.preventDefault()
    onSubmit({ origin, destination, travelDate })
  }

  return (
    <form onSubmit={handleSubmit}>
      <h2>Plan your route</h2>

      <div>
        <label htmlFor="origin">Origin</label>
        <input
          id="origin"
          type="text"
          placeholder="e.g. Madrid"
          value={origin}
          onChange={(e) => setOrigin(e.target.value)}
          required
        />
      </div>

      <div>
        <label htmlFor="destination">Destination</label>
        <input
          id="destination"
          type="text"
          placeholder="e.g. Barcelona"
          value={destination}
          onChange={(e) => setDestination(e.target.value)}
          required
        />
      </div>

      <div>
        <label htmlFor="travelDate">Travel date</label>
        <input
          id="travelDate"
          type="date"
          min={today}
          value={travelDate}
          onChange={(e) => setTravelDate(e.target.value)}
          required
        />
      </div>

      <button type="submit" disabled={loading}>
        {loading ? 'Loading…' : 'Get weather forecast'}
      </button>
    </form>
  )
}

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
    <form className="route-form" onSubmit={handleSubmit} aria-label="Route form">
      <h2>Plan your route</h2>
      <div className="form-row">
        <div className="form-field">
          <label htmlFor="origin">From</label>
          <input
            id="origin"
            type="text"
            placeholder="e.g. Madrid"
            value={origin}
            onChange={(e) => setOrigin(e.target.value)}
            required
          />
        </div>

        <div className="form-field">
          <label htmlFor="destination">To</label>
          <input
            id="destination"
            type="text"
            placeholder="e.g. Barcelona"
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
            required
          />
        </div>

        <div className="form-field">
          <label htmlFor="travelDate">Date</label>
          <input
            id="travelDate"
            type="date"
            min={today}
            value={travelDate}
            onChange={(e) => setTravelDate(e.target.value)}
            required
          />
        </div>

        <button type="submit" className="submit-btn" disabled={loading}>
          {loading ? 'Loading…' : 'Get forecast'}
        </button>
      </div>
    </form>
  )
}

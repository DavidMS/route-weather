import { render, screen, fireEvent } from '@testing-library/react'
import { describe, it, expect, vi } from 'vitest'
import { RouteForm } from './RouteForm'

describe('RouteForm', () => {
  it('renders origin, destination and date inputs', () => {
    render(<RouteForm onSubmit={vi.fn()} loading={false} />)

    expect(screen.getByLabelText('From')).toBeInTheDocument()
    expect(screen.getByLabelText('To')).toBeInTheDocument()
    expect(screen.getByLabelText('Date')).toBeInTheDocument()
  })

  it('calls onSubmit with form values when submitted', () => {
    const onSubmit = vi.fn()
    render(<RouteForm onSubmit={onSubmit} loading={false} />)

    fireEvent.change(screen.getByLabelText('From'), { target: { value: 'Madrid' } })
    fireEvent.change(screen.getByLabelText('To'), { target: { value: 'Barcelona' } })
    fireEvent.change(screen.getByLabelText('Date'), { target: { value: '2026-03-01' } })
    fireEvent.submit(screen.getByRole('form', { name: 'Route form' }))

    expect(onSubmit).toHaveBeenCalledOnce()
    expect(onSubmit).toHaveBeenCalledWith({
      origin: 'Madrid',
      destination: 'Barcelona',
      travelDate: '2026-03-01',
    })
  })

  it('disables submit button while loading', () => {
    render(<RouteForm onSubmit={vi.fn()} loading={true} />)

    expect(screen.getByRole('button')).toBeDisabled()
    expect(screen.getByRole('button')).toHaveTextContent('Loading…')
  })

  it('shows "Get forecast" label when not loading', () => {
    render(<RouteForm onSubmit={vi.fn()} loading={false} />)

    expect(screen.getByRole('button')).not.toBeDisabled()
    expect(screen.getByRole('button')).toHaveTextContent('Get forecast')
  })
})

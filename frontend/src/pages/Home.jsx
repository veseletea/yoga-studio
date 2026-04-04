import { useState, useEffect } from 'react'
import { studentApi, instructorApi, yogaClassApi, bookingApi } from '../api'

export default function Home() {
  const [stats, setStats] = useState({ students: 0, instructors: 0, classes: 0, bookings: 0 })

  useEffect(() => {
    Promise.all([
      studentApi.getAll(),
      instructorApi.getAll(),
      yogaClassApi.getAll(),
      bookingApi.getAll(),
    ]).then(([s, i, c, b]) =>
      setStats({ students: s.length, instructors: i.length, classes: c.length, bookings: b.length })
    )
  }, [])

  return (
    <div className="page">
      <div className="home-hero">
        <h1>Yoga Studio</h1>
        <p>Sistem de management pentru studioul tău de yoga</p>
      </div>
      <div className="home-stats">
        <div className="stat-card">
          <div className="number">{stats.instructors}</div>
          <div className="label">Instructori</div>
        </div>
        <div className="stat-card">
          <div className="number">{stats.students}</div>
          <div className="label">Studenți</div>
        </div>
        <div className="stat-card">
          <div className="number">{stats.classes}</div>
          <div className="label">Clase</div>
        </div>
        <div className="stat-card">
          <div className="number">{stats.bookings}</div>
          <div className="label">Rezervări</div>
        </div>
      </div>
    </div>
  )
}

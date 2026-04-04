import { useState, useEffect } from 'react'
import { bookingApi, studentApi, yogaClassApi } from '../api'

export default function BookingsPage() {
  const [items, setItems] = useState([])
  const [students, setStudents] = useState([])
  const [classes, setClasses] = useState([])
  const [form, setForm] = useState({ studentId: '', yogaClassId: '' })
  const [error, setError] = useState('')

  const load = () => {
    bookingApi.getAll().then(setItems).catch(e => setError(e.message))
    studentApi.getAll().then(setStudents)
    yogaClassApi.getAll().then(setClasses)
  }

  useEffect(() => { load() }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await bookingApi.create({ studentId: Number(form.studentId), yogaClassId: Number(form.yogaClassId) })
      setForm({ studentId: '', yogaClassId: '' })
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  const handleCancel = async (id) => {
    try {
      await bookingApi.cancel(id)
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  const badgeClass = (status) => {
    if (status === 'CONFIRMED') return 'badge badge-confirmed'
    if (status === 'CANCELLED') return 'badge badge-cancelled'
    return 'badge badge-waitlisted'
  }

  const statusLabel = (status) => {
    if (status === 'CONFIRMED') return 'Confirmat'
    if (status === 'CANCELLED') return 'Anulat'
    return 'Lista de așteptare'
  }

  return (
    <div className="page">
      <h1>Rezervări</h1>

      {error && <div className="error">{error}</div>}

      <div className="card">
        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <div className="form-group">
              <label>Student</label>
              <select value={form.studentId} onChange={e => setForm({ ...form, studentId: e.target.value })} required>
                <option value="">-- Alege student --</option>
                {students.map(s => (
                  <option key={s.id} value={s.id}>{s.firstName} {s.lastName}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Clasă</label>
              <select value={form.yogaClassId} onChange={e => setForm({ ...form, yogaClassId: e.target.value })} required>
                <option value="">-- Alege clasă --</option>
                {classes.map(c => (
                  <option key={c.id} value={c.id}>{c.name} ({c.currentBookings}/{c.maxCapacity})</option>
                ))}
              </select>
            </div>
          </div>
          <button type="submit" className="btn btn-primary">Rezervă</button>
        </form>
      </div>

      <div className="card">
        {items.length === 0 ? (
          <div className="empty">Nicio rezervare</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Student</th>
                <th>Clasă</th>
                <th>Data rezervării</th>
                <th>Status</th>
                <th>Acțiuni</th>
              </tr>
            </thead>
            <tbody>
              {items.map(item => (
                <tr key={item.id}>
                  <td>{item.studentName}</td>
                  <td>{item.className}</td>
                  <td>{new Date(item.bookedAt).toLocaleString('ro-RO')}</td>
                  <td><span className={badgeClass(item.status)}>{statusLabel(item.status)}</span></td>
                  <td>
                    {item.status === 'CONFIRMED' && (
                      <button className="btn btn-danger btn-sm" onClick={() => handleCancel(item.id)}>Anulează</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}

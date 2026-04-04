import { useState, useEffect } from 'react'
import { yogaClassApi, instructorApi } from '../api'

const days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']
const dayLabels = { MONDAY: 'Luni', TUESDAY: 'Marți', WEDNESDAY: 'Miercuri', THURSDAY: 'Joi', FRIDAY: 'Vineri', SATURDAY: 'Sâmbătă', SUNDAY: 'Duminică' }

const empty = { name: '', description: '', dayOfWeek: 'MONDAY', startTime: '10:00', durationMinutes: 60, maxCapacity: 10, instructorId: '' }

export default function ClassesPage() {
  const [items, setItems] = useState([])
  const [instructors, setInstructors] = useState([])
  const [form, setForm] = useState(empty)
  const [editId, setEditId] = useState(null)
  const [error, setError] = useState('')

  const load = () => {
    yogaClassApi.getAll().then(setItems).catch(e => setError(e.message))
    instructorApi.getAll().then(setInstructors)
  }

  useEffect(() => { load() }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const data = { ...form, durationMinutes: Number(form.durationMinutes), maxCapacity: Number(form.maxCapacity), instructorId: Number(form.instructorId) }
      if (editId) {
        await yogaClassApi.update(editId, data)
      } else {
        await yogaClassApi.create(data)
      }
      setForm(empty)
      setEditId(null)
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  const handleEdit = (item) => {
    setEditId(item.id)
    setForm({
      name: item.name,
      description: item.description || '',
      dayOfWeek: item.dayOfWeek,
      startTime: item.startTime.substring(0, 5),
      durationMinutes: item.durationMinutes,
      maxCapacity: item.maxCapacity,
      instructorId: item.instructor.id,
    })
  }

  const handleDelete = async (id) => {
    if (!confirm('Ștergi această clasă?')) return
    try {
      await yogaClassApi.delete(id)
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="page">
      <h1>Clase Yoga</h1>

      {error && <div className="error">{error}</div>}

      <div className="card">
        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <div className="form-group">
              <label>Nume clasă</label>
              <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>Instructor</label>
              <select value={form.instructorId} onChange={e => setForm({ ...form, instructorId: e.target.value })} required>
                <option value="">-- Alege --</option>
                {instructors.map(i => (
                  <option key={i.id} value={i.id}>{i.firstName} {i.lastName}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Zi</label>
              <select value={form.dayOfWeek} onChange={e => setForm({ ...form, dayOfWeek: e.target.value })}>
                {days.map(d => <option key={d} value={d}>{dayLabels[d]}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label>Ora start</label>
              <input type="time" value={form.startTime} onChange={e => setForm({ ...form, startTime: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>Durata (minute)</label>
              <input type="number" min="15" value={form.durationMinutes} onChange={e => setForm({ ...form, durationMinutes: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>Capacitate max</label>
              <input type="number" min="1" value={form.maxCapacity} onChange={e => setForm({ ...form, maxCapacity: e.target.value })} required />
            </div>
            <div className="form-group full-width">
              <label>Descriere</label>
              <input value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} />
            </div>
          </div>
          <div className="btn-group">
            <button type="submit" className="btn btn-primary">{editId ? 'Salvează' : 'Adaugă'}</button>
            {editId && <button type="button" className="btn btn-warning" onClick={() => { setEditId(null); setForm(empty) }}>Anulează</button>}
          </div>
        </form>
      </div>

      <div className="card">
        {items.length === 0 ? (
          <div className="empty">Nicio clasă adăugată</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Clasă</th>
                <th>Zi</th>
                <th>Ora</th>
                <th>Durata</th>
                <th>Instructor</th>
                <th>Locuri</th>
                <th>Acțiuni</th>
              </tr>
            </thead>
            <tbody>
              {items.map(item => (
                <tr key={item.id}>
                  <td>{item.name}</td>
                  <td>{dayLabels[item.dayOfWeek]}</td>
                  <td>{item.startTime.substring(0, 5)}</td>
                  <td>{item.durationMinutes} min</td>
                  <td>{item.instructor.firstName} {item.instructor.lastName}</td>
                  <td>{item.currentBookings}/{item.maxCapacity}</td>
                  <td>
                    <div className="btn-group">
                      <button className="btn btn-primary btn-sm" onClick={() => handleEdit(item)}>Editează</button>
                      <button className="btn btn-danger btn-sm" onClick={() => handleDelete(item.id)}>Șterge</button>
                    </div>
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

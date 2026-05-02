import { useState, useEffect } from 'react'
import { studentApi } from '../api'

const empty = { firstName: '', lastName: '', email: '', phone: '' }

export default function StudentsPage() {
  const [items, setItems] = useState([])
  const [form, setForm] = useState(empty)
  const [editId, setEditId] = useState(null)
  const [error, setError] = useState('')

  const load = () => studentApi.getAll().then(setItems).catch(e => setError(e.message))

  useEffect(() => { load() }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      if (editId) {
        await studentApi.update(editId, form)
      } else {
        await studentApi.create(form)
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
    setForm({ firstName: item.firstName, lastName: item.lastName, email: item.email, phone: item.phone || '' })
  }

  const handleDelete = async (id) => {
    if (!confirm('Delete this student?')) return
    try {
      await studentApi.delete(id)
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="page">
      <h1>Students</h1>

      {error && <div className="error">{error}</div>}

      <div className="card">
        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <div className="form-group">
              <label>First Name</label>
              <input value={form.firstName} onChange={e => setForm({ ...form, firstName: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>Last Name</label>
              <input value={form.lastName} onChange={e => setForm({ ...form, lastName: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>Email</label>
              <input type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>Phone</label>
              <input value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} />
            </div>
          </div>
          <div className="btn-group">
            <button type="submit" className="btn btn-primary">{editId ? 'Save' : 'Add'}</button>
            {editId && <button type="button" className="btn btn-warning" onClick={() => { setEditId(null); setForm(empty) }}>Cancel</button>}
          </div>
        </form>
      </div>

      <div className="card">
        {items.length === 0 ? (
          <div className="empty">No students added yet</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Member Since</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {items.map(item => (
                <tr key={item.id}>
                  <td>{item.firstName} {item.lastName}</td>
                  <td>{item.email}</td>
                  <td>{item.phone}</td>
                  <td>{item.memberSince}</td>
                  <td>
                    <div className="btn-group">
                      <button className="btn btn-primary btn-sm" onClick={() => handleEdit(item)}>Edit</button>
                      <button className="btn btn-danger btn-sm" onClick={() => handleDelete(item.id)}>Delete</button>
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

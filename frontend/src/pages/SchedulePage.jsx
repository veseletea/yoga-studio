import { useState, useEffect } from 'react';
import { yogaClassApi, studentApi, bookingApi } from '../api';

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
const DAY_LABELS = {
  MONDAY: 'Monday', TUESDAY: 'Tuesday', WEDNESDAY: 'Wednesday',
  THURSDAY: 'Thursday', FRIDAY: 'Friday', SATURDAY: 'Saturday', SUNDAY: 'Sunday'
};

export default function SchedulePage() {
  const [classes, setClasses] = useState([]);
  const [selected, setSelected] = useState(null);
  const [form, setForm] = useState({ firstName: '', lastName: '', email: '' });
  const [message, setMessage] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    yogaClassApi.getAll().then(setClasses).catch(() => {});
  }, []);

  const classesByDay = DAYS.map(day => ({
    day,
    label: DAY_LABELS[day],
    items: classes.filter(c => c.dayOfWeek === day).sort((a, b) => a.startTime.localeCompare(b.startTime))
  })).filter(g => g.items.length > 0);

  const spotsLeft = (c) => c.maxCapacity - c.currentBookings;

  async function handleBook(e) {
    e.preventDefault();
    setLoading(true);
    setMessage(null);

    try {
      let student;
      try {
        student = await studentApi.findByEmail(form.email);
      } catch {
        student = await studentApi.create({
          firstName: form.firstName,
          lastName: form.lastName,
          email: form.email
        });
      }

      const booking = await bookingApi.create({
        studentId: student.id,
        yogaClassId: selected.id
      });

      const statusText = booking.status === 'CONFIRMED'
        ? 'Booking confirmed!'
        : 'Class is full. You have been added to the waitlist.';

      setMessage({ type: 'success', text: statusText });
      setForm({ firstName: '', lastName: '', email: '' });

      const updated = await yogaClassApi.getAll();
      setClasses(updated);
    } catch (err) {
      setMessage({ type: 'error', text: err.message });
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page">
      <div className="schedule-header">
        <h1>Class Schedule</h1>
        <p>Choose a class and book your spot</p>
      </div>

      <div className="schedule-grid">
        {classesByDay.map(({ day, label, items }) => (
          <div key={day} className="schedule-day">
            <h3 className="day-title">{label}</h3>
            {items.map(c => (
              <div
                key={c.id}
                className={`class-card ${selected?.id === c.id ? 'class-card-selected' : ''}`}
                onClick={() => { setSelected(c); setMessage(null); }}
              >
                <div className="class-card-time">{c.startTime.slice(0, 5)}</div>
                <div className="class-card-info">
                  <div className="class-card-name">{c.name}</div>
                  <div className="class-card-instructor">
                    {c.instructor.firstName} {c.instructor.lastName}
                  </div>
                </div>
                <div className="class-card-meta">
                  <span className="class-card-duration">{c.durationMinutes} min</span>
                  <span className={`class-card-spots ${spotsLeft(c) <= 2 ? 'spots-low' : ''}`}>
                    {spotsLeft(c) > 0 ? `${spotsLeft(c)} spots` : 'Waitlist'}
                  </span>
                </div>
              </div>
            ))}
          </div>
        ))}
      </div>

      {selected && (
        <div className="modal-overlay" onClick={() => { setSelected(null); setMessage(null); }}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <button className="modal-close" onClick={() => { setSelected(null); setMessage(null); }}>&times;</button>

            <div className="modal-header">
              <h2>{selected.name}</h2>
              <p className="modal-subtitle">
                {DAY_LABELS[selected.dayOfWeek]} &middot; {selected.startTime.slice(0, 5)} &middot; {selected.durationMinutes} min
              </p>
            </div>

            <div className="modal-details">
              <div className="detail-row">
                <span className="detail-label">Instructor</span>
                <span>{selected.instructor.firstName} {selected.instructor.lastName}</span>
              </div>
              {selected.description && (
                <div className="detail-row">
                  <span className="detail-label">Description</span>
                  <span>{selected.description}</span>
                </div>
              )}
              <div className="detail-row">
                <span className="detail-label">Capacity</span>
                <span>{selected.currentBookings} / {selected.maxCapacity} spots taken</span>
              </div>
            </div>

            {message && (
              <div className={`schedule-message ${message.type}`}>
                {message.text}
              </div>
            )}

            <form onSubmit={handleBook} className="booking-form">
              <h3>Book a Spot</h3>
              <div className="form-grid">
                <div className="form-group">
                  <label>First Name</label>
                  <input
                    required
                    value={form.firstName}
                    onChange={e => setForm({ ...form, firstName: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Last Name</label>
                  <input
                    required
                    value={form.lastName}
                    onChange={e => setForm({ ...form, lastName: e.target.value })}
                  />
                </div>
                <div className="form-group full-width">
                  <label>Email</label>
                  <input
                    type="email"
                    required
                    value={form.email}
                    onChange={e => setForm({ ...form, email: e.target.value })}
                  />
                </div>
              </div>
              <button type="submit" className="btn btn-primary btn-book" disabled={loading}>
                {loading ? 'Processing...' : 'Book Now'}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

const API = '/api';

async function request(url, options = {}) {
  const res = await fetch(url, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!res.ok) {
    const error = await res.json().catch(() => ({ detail: 'Unknown error' }));
    throw new Error(error.detail || `HTTP ${res.status}`);
  }
  if (res.status === 204) return null;
  return res.json();
}

export const studentApi = {
  getAll: () => request(`${API}/students`),
  findByEmail: (email) => request(`${API}/students/search?email=${encodeURIComponent(email)}`),
  create: (data) => request(`${API}/students`, { method: 'POST', body: JSON.stringify(data) }),
  update: (id, data) => request(`${API}/students/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id) => request(`${API}/students/${id}`, { method: 'DELETE' }),
};

export const instructorApi = {
  getAll: () => request(`${API}/instructors`),
  create: (data) => request(`${API}/instructors`, { method: 'POST', body: JSON.stringify(data) }),
  update: (id, data) => request(`${API}/instructors/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id) => request(`${API}/instructors/${id}`, { method: 'DELETE' }),
};

export const yogaClassApi = {
  getAll: () => request(`${API}/classes`),
  create: (data) => request(`${API}/classes`, { method: 'POST', body: JSON.stringify(data) }),
  update: (id, data) => request(`${API}/classes/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id) => request(`${API}/classes/${id}`, { method: 'DELETE' }),
};

export const bookingApi = {
  getAll: () => request(`${API}/bookings`),
  create: (data) => request(`${API}/bookings`, { method: 'POST', body: JSON.stringify(data) }),
  cancel: (id) => request(`${API}/bookings/${id}/cancel`, { method: 'PATCH' }),
};

import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Home from './pages/Home'
import InstructorsPage from './pages/InstructorsPage'
import StudentsPage from './pages/StudentsPage'
import ClassesPage from './pages/ClassesPage'
import BookingsPage from './pages/BookingsPage'
import SchedulePage from './pages/SchedulePage'

export default function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/schedule" element={<SchedulePage />} />
        <Route path="/instructors" element={<InstructorsPage />} />
        <Route path="/students" element={<StudentsPage />} />
        <Route path="/classes" element={<ClassesPage />} />
        <Route path="/bookings" element={<BookingsPage />} />
      </Routes>
    </>
  )
}

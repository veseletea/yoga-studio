import { NavLink } from 'react-router-dom';

export default function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-brand">Yoga Studio</div>
      <div className="navbar-links">
        <NavLink to="/">Home</NavLink>
        <NavLink to="/schedule">Schedule</NavLink>
        <NavLink to="/instructors">Instructors</NavLink>
        <NavLink to="/students">Students</NavLink>
        <NavLink to="/classes">Classes</NavLink>
        <NavLink to="/bookings">Bookings</NavLink>
      </div>
    </nav>
  );
}

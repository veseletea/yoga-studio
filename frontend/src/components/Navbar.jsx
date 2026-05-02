import { NavLink } from 'react-router-dom';

export default function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-brand">Yoga Studio</div>
      <div className="navbar-links">
        <NavLink to="/">Acasă</NavLink>
        <NavLink to="/schedule">Program</NavLink>
        <NavLink to="/instructors">Instructori</NavLink>
        <NavLink to="/students">Studenți</NavLink>
        <NavLink to="/classes">Clase</NavLink>
        <NavLink to="/bookings">Rezervări</NavLink>
      </div>
    </nav>
  );
}

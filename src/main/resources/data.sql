-- Instructori
INSERT INTO instructors (first_name, last_name, email, phone, specialization) VALUES
('Ana', 'Popescu', 'ana.popescu@yoga.ro', '0722111222', 'Hatha Yoga'),
('Mihai', 'Dumitrescu', 'mihai.d@yoga.ro', '0733222333', 'Vinyasa Flow'),
('Elena', 'Marinescu', 'elena.m@yoga.ro', '0744333444', 'Ashtanga Yoga'),
('Radu', 'Ionescu', 'radu.i@yoga.ro', '0755444555', 'Yin Yoga');

-- Studenți
INSERT INTO students (first_name, last_name, email, phone, member_since) VALUES
('Maria', 'Gheorghe', 'maria.g@email.ro', '0766111222', '2025-09-01'),
('Andrei', 'Popa', 'andrei.p@email.ro', '0766222333', '2025-10-15'),
('Ioana', 'Stan', 'ioana.s@email.ro', '0766333444', '2026-01-10'),
('Cristian', 'Radu', 'cristian.r@email.ro', '0766444555', '2026-02-20'),
('Laura', 'Dinu', 'laura.d@email.ro', '0766555666', '2026-03-01'),
('Vlad', 'Munteanu', 'vlad.m@email.ro', '0766666777', '2026-03-15');

-- Clase Yoga
INSERT INTO yoga_classes (name, description, day_of_week, start_time, duration_minutes, max_capacity, instructor_id) VALUES
('Hatha Dimineața', 'Clasă pentru începători, ritm lent', 'MONDAY', '08:00:00', 60, 12, 1),
('Vinyasa Flow', 'Flux dinamic, nivel intermediar', 'MONDAY', '10:00:00', 75, 10, 2),
('Ashtanga Primary', 'Seria primară Ashtanga', 'TUESDAY', '07:00:00', 90, 8, 3),
('Yin Relax', 'Stretching profund, relaxare', 'TUESDAY', '18:00:00', 60, 15, 4),
('Hatha Seara', 'Clasă de seară, toate nivelurile', 'WEDNESDAY', '19:00:00', 60, 12, 1),
('Power Vinyasa', 'Clasă intensă, nivel avansat', 'THURSDAY', '10:00:00', 75, 10, 2),
('Yoga pentru Spate', 'Focus pe coloană și postură', 'FRIDAY', '09:00:00', 60, 12, 1),
('Meditație și Pranayama', 'Respirație și meditație ghidată', 'SATURDAY', '08:00:00', 45, 20, 3),
('Yoga în Parc', 'Clasă în aer liber (vara)', 'SUNDAY', '09:00:00', 60, 25, 2);

-- Rezervări
INSERT INTO bookings (student_id, yoga_class_id, booked_at, status) VALUES
(1, 1, '2026-04-01 08:30:00', 'CONFIRMED'),
(2, 1, '2026-04-01 09:00:00', 'CONFIRMED'),
(3, 2, '2026-04-01 10:15:00', 'CONFIRMED'),
(4, 3, '2026-04-01 12:00:00', 'CONFIRMED'),
(5, 4, '2026-04-02 14:00:00', 'CONFIRMED'),
(6, 4, '2026-04-02 15:30:00', 'CONFIRMED'),
(1, 5, '2026-04-02 16:00:00', 'CONFIRMED'),
(2, 6, '2026-04-03 08:00:00', 'CONFIRMED'),
(3, 7, '2026-04-03 09:00:00', 'CONFIRMED'),
(4, 8, '2026-04-03 10:00:00', 'CONFIRMED'),
(5, 9, '2026-04-03 11:00:00', 'CONFIRMED'),
(1, 9, '2026-04-03 11:30:00', 'CONFIRMED'),
(6, 1, '2026-04-03 12:00:00', 'CANCELLED');

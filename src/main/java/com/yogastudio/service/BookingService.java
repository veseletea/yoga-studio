package com.yogastudio.service;

import com.yogastudio.dto.BookingRequest;
import com.yogastudio.dto.BookingResponse;
import com.yogastudio.entity.Booking;
import com.yogastudio.entity.Student;
import com.yogastudio.entity.YogaClass;
import com.yogastudio.exception.DuplicateResourceException;
import com.yogastudio.exception.ResourceNotFoundException;
import com.yogastudio.repository.BookingRepository;
import com.yogastudio.repository.StudentRepository;
import com.yogastudio.repository.YogaClassRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final StudentRepository studentRepository;
    private final YogaClassRepository yogaClassRepository;

    public BookingService(BookingRepository bookingRepository,
                          StudentRepository studentRepository,
                          YogaClassRepository yogaClassRepository) {
        this.bookingRepository = bookingRepository;
        this.studentRepository = studentRepository;
        this.yogaClassRepository = yogaClassRepository;
    }

    public List<BookingResponse> findAll() {
        return bookingRepository.findAll().stream()
                .map(BookingResponse::from)
                .toList();
    }

    public List<BookingResponse> findByStudent(Long studentId) {
        return bookingRepository.findByStudentId(studentId).stream()
                .map(BookingResponse::from)
                .toList();
    }

    public List<BookingResponse> findByYogaClass(Long yogaClassId) {
        return bookingRepository.findByYogaClassId(yogaClassId).stream()
                .map(BookingResponse::from)
                .toList();
    }

    @Transactional
    public BookingResponse create(BookingRequest request) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student cu id " + request.studentId() + " nu a fost găsit"));

        YogaClass yogaClass = yogaClassRepository.findById(request.yogaClassId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Clasa cu id " + request.yogaClassId() + " nu a fost găsită"));

        if (bookingRepository.existsByStudentIdAndYogaClassId(request.studentId(), request.yogaClassId())) {
            throw new DuplicateResourceException("Studentul este deja înscris la această clasă");
        }

        long currentBookings = bookingRepository.findByYogaClassId(request.yogaClassId()).stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .count();

        Booking.BookingStatus status = currentBookings >= yogaClass.getMaxCapacity()
                ? Booking.BookingStatus.WAITLISTED
                : Booking.BookingStatus.CONFIRMED;

        var booking = new Booking();
        booking.setStudent(student);
        booking.setYogaClass(yogaClass);
        booking.setBookedAt(LocalDateTime.now());
        booking.setStatus(status);

        return BookingResponse.from(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse cancel(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rezervare cu id " + id + " nu a fost găsită"));

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        return BookingResponse.from(bookingRepository.save(booking));
    }
}

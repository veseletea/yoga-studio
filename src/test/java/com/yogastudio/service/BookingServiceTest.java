package com.yogastudio.service;

import com.yogastudio.dto.BookingRequest;
import com.yogastudio.entity.Booking;
import com.yogastudio.entity.Student;
import com.yogastudio.entity.YogaClass;
import com.yogastudio.exception.DuplicateResourceException;
import com.yogastudio.exception.ResourceNotFoundException;
import com.yogastudio.repository.BookingRepository;
import com.yogastudio.repository.StudentRepository;
import com.yogastudio.repository.YogaClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private YogaClassRepository yogaClassRepository;

    @InjectMocks private BookingService bookingService;

    @Captor private ArgumentCaptor<Booking> bookingCaptor;

    private Student student;
    private YogaClass yogaClass;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setFirstName("Ana");
        student.setLastName("Popescu");
        student.setEmail("ana@example.com");

        yogaClass = new YogaClass();
        yogaClass.setId(10L);
        yogaClass.setName("Hatha Morning");
        yogaClass.setMaxCapacity(2);
    }

    @Test
    void confirmsBookingWhenSpotsAreAvailable() {
        var request = new BookingRequest(1L, 10L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(yogaClassRepository.findById(10L)).thenReturn(Optional.of(yogaClass));
        when(bookingRepository.existsByStudentIdAndYogaClassId(1L, 10L)).thenReturn(false);
        when(bookingRepository.findByYogaClassId(10L)).thenReturn(List.of(confirmedBooking()));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        bookingService.create(request);

        verify(bookingRepository).save(bookingCaptor.capture());
        assertThat(bookingCaptor.getValue().getStatus())
                .isEqualTo(Booking.BookingStatus.CONFIRMED);
    }

    @Test
    void waitlistsBookingWhenClassIsFull() {
        var request = new BookingRequest(1L, 10L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(yogaClassRepository.findById(10L)).thenReturn(Optional.of(yogaClass));
        when(bookingRepository.existsByStudentIdAndYogaClassId(1L, 10L)).thenReturn(false);
        when(bookingRepository.findByYogaClassId(10L))
                .thenReturn(List.of(confirmedBooking(), confirmedBooking()));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        bookingService.create(request);

        verify(bookingRepository).save(bookingCaptor.capture());
        assertThat(bookingCaptor.getValue().getStatus())
                .isEqualTo(Booking.BookingStatus.WAITLISTED);
    }

    @Test
    void ignoresCancelledBookingsWhenCountingCapacity() {
        var request = new BookingRequest(1L, 10L);

        var cancelled = new Booking();
        cancelled.setStatus(Booking.BookingStatus.CANCELLED);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(yogaClassRepository.findById(10L)).thenReturn(Optional.of(yogaClass));
        when(bookingRepository.existsByStudentIdAndYogaClassId(1L, 10L)).thenReturn(false);
        when(bookingRepository.findByYogaClassId(10L))
                .thenReturn(List.of(confirmedBooking(), cancelled));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        bookingService.create(request);

        verify(bookingRepository).save(bookingCaptor.capture());
        assertThat(bookingCaptor.getValue().getStatus())
                .isEqualTo(Booking.BookingStatus.CONFIRMED);
    }

    @Test
    void rejectsDuplicateBooking() {
        var request = new BookingRequest(1L, 10L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(yogaClassRepository.findById(10L)).thenReturn(Optional.of(yogaClass));
        when(bookingRepository.existsByStudentIdAndYogaClassId(1L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already enrolled");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void failsWhenStudentDoesNotExist() {
        var request = new BookingRequest(99L, 10L);

        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(bookingRepository, never()).save(any());
    }

    private Booking confirmedBooking() {
        var booking = new Booking();
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        return booking;
    }
}
package com.yogastudio.repository;

import com.yogastudio.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStudentId(Long studentId);
    List<Booking> findByYogaClassId(Long yogaClassId);
    boolean existsByStudentIdAndYogaClassId(Long studentId, Long yogaClassId);
}

package com.yogastudio.repository;

import com.yogastudio.entity.YogaClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface YogaClassRepository extends JpaRepository<YogaClass, Long> {
    List<YogaClass> findByDayOfWeek(DayOfWeek dayOfWeek);
    List<YogaClass> findByInstructorId(Long instructorId);
}

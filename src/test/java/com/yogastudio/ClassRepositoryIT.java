package com.yogastudio;

import com.yogastudio.entity.Instructor;
import com.yogastudio.repository.InstructorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yogastudio.entity.YogaClass;
import com.yogastudio.repository.YogaClassRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private YogaClassRepository repository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Test
    void savesAndFindsClass() {
        var instructor = new Instructor();
        instructor.setFirstName("Ana");
        instructor.setLastName("Popescu");
        instructor.setEmail("Popescu@yahoo.com");
        instructor.setPhone("0763423411");



        var savedInstructor = instructorRepository.save(instructor);

        var yogaClass = new YogaClass();
        yogaClass.setMaxCapacity(10);
        yogaClass.setDayOfWeek(DayOfWeek.MONDAY);
        yogaClass.setStartTime(LocalTime.of(10, 0));
        yogaClass.setDurationMinutes(60);
        yogaClass.setDescription("Test class");
        yogaClass.setName("Hatha Morning");
        yogaClass.setInstructor(savedInstructor);

        var saved = repository.save(yogaClass);

        assertThat(repository.findById(saved.getId())).isPresent();
    }
}


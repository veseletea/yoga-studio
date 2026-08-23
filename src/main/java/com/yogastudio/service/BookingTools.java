package com.yogastudio.service;

import com.yogastudio.dto.BookingRequest;
import com.yogastudio.entity.Student;
import com.yogastudio.entity.YogaClass;
import com.yogastudio.exception.DuplicateResourceException;
import com.yogastudio.repository.StudentRepository;
import com.yogastudio.repository.YogaClassRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Locale;

/**
 * Tools exposed to the AI assistant for structured studio data.
 *
 * Security note: the identity of the acting user is always resolved from the
 * Spring Security context, never from a model-supplied parameter. This prevents
 * prompt injection from booking classes on behalf of another student.
 */
@Component
public class BookingTools {

    private static final Logger log = LoggerFactory.getLogger(BookingTools.class);

    private final YogaClassRepository yogaClassRepository;
    private final StudentRepository studentRepository;
    private final BookingService bookingService;

    public BookingTools(YogaClassRepository yogaClassRepository,
                        StudentRepository studentRepository,
                        BookingService bookingService) {
        this.yogaClassRepository = yogaClassRepository;
        this.studentRepository = studentRepository;
        this.bookingService = bookingService;
    }

    @Tool(description = "Get the yoga class schedule. Optionally filter by a day of the week.")
    public String findClasses(
            @ToolParam(required = false,
                    description = "Day of week in English, e.g. MONDAY. Omit for the full schedule.")
            String dayOfWeek) {

        log.info("Tool called: findClasses(dayOfWeek={})", dayOfWeek);

        List<YogaClass> classes = yogaClassRepository.findAll();

        if (dayOfWeek != null && !dayOfWeek.isBlank()) {
            DayOfWeek day;
            try {
                day = DayOfWeek.valueOf(dayOfWeek.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                return "Unknown day: " + dayOfWeek;
            }
            classes = classes.stream()
                    .filter(c -> c.getDayOfWeek() == day)
                    .toList();
        }

        if (classes.isEmpty()) {
            return "No classes found.";
        }

        return classes.stream()
                .map(this::describe)
                .reduce((a, b) -> a + "\n" + b)
                .orElse("No classes found.");
    }

    @Tool(description = "Book the currently authenticated student into a yoga class by class name.")
    public String bookClass(
            @ToolParam(description = "The exact name of the yoga class")
            String className) {

        Student student = currentStudent();
        if (student == null) {
            return "You need to be signed in to make a booking.";
        }

        log.info("Tool called: bookClass(className={}) for student id {}", className, student.getId());

        YogaClass yogaClass = findByName(className);
        if (yogaClass == null) {
            return "No class found with the name: " + className;
        }

        try {
            var response = bookingService.create(
                    new BookingRequest(student.getId(), yogaClass.getId()));
            return "Booking created for %s. Status: %s"
                    .formatted(yogaClass.getName(), response.status());
        } catch (DuplicateResourceException e) {
            return "You are already enrolled in " + yogaClass.getName() + ".";
        }
    }

    @Tool(description = "List the bookings of the currently authenticated student.")
    public String myBookings() {

        Student student = currentStudent();
        if (student == null) {
            return "You need to be signed in to see your bookings.";
        }

        log.info("Tool called: myBookings() for student id {}", student.getId());

        var bookings = bookingService.findByStudent(student.getId());
        if (bookings.isEmpty()) {
            return "You have no bookings yet.";
        }

        return bookings.stream()
                .map(b -> "%s — status %s".formatted(b.className(), b.status()))
                .reduce((a, b) -> a + "\n" + b)
                .orElse("You have no bookings yet.");
    }

    // --- helpers ---

    /**
     * Resolves the acting student from the authenticated security context.
     * Returns null when the request is anonymous.
     */
    private Student currentStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        return studentRepository.findByEmail(authentication.getName()).orElse(null);
    }

    private YogaClass findByName(String className) {
        if (className == null || className.isBlank()) {
            return null;
        }
        return yogaClassRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(className.trim()))
                .findFirst()
                .orElse(null);
    }

    private String describe(YogaClass c) {
        return "%s — %s at %s, %d min, instructor %s %s, capacity %d"
                .formatted(
                        c.getDayOfWeek(),
                        c.getName(),
                        c.getStartTime(),
                        c.getDurationMinutes(),
                        c.getInstructor().getFirstName(),
                        c.getInstructor().getLastName(),
                        c.getMaxCapacity());
    }
}
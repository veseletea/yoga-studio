package com.yogastudio.service;

import com.yogastudio.entity.YogaClass;
import com.yogastudio.repository.YogaClassRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Locale;

@Component
public class BookingTools {

    private static final Logger log = LoggerFactory.getLogger(BookingTools.class);

    private final YogaClassRepository yogaClassRepository;

    public BookingTools(YogaClassRepository yogaClassRepository) {
        this.yogaClassRepository = yogaClassRepository;
    }

    @Tool(description = "Get the yoga class schedule. Optionally filter by a day of the week.")
    public String findClasses(
            @ToolParam(required = false, description = "Day of week in English, e.g. MONDAY. Omit for the full schedule.")
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
                .map(c -> "%s — %s at %s, %d min, instructor %s %s, capacity %d"
                        .formatted(
                                c.getDayOfWeek(),
                                c.getName(),
                                c.getStartTime(),
                                c.getDurationMinutes(),
                                c.getInstructor().getFirstName(),
                                c.getInstructor().getLastName(),
                                c.getMaxCapacity()))
                .reduce((a, b) -> a + "\n" + b)
                .orElse("No classes found.");
    }
}
package com.yogastudio.config;

import com.yogastudio.entity.Student;
import com.yogastudio.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminSeeder(StudentRepository studentRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${app.admin.email:admin@yogastudio.local}") String adminEmail,
                       @Value("${app.admin.password:}") String adminPassword) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminPassword == null || adminPassword.isBlank()) {
            log.warn("app.admin.password not set — skipping admin seeding.");
            return;
        }

        if (studentRepository.findByEmail(adminEmail).isPresent()) {
            log.info("Admin account already exists. Skipping.");
            return;
        }

        var admin = new Student();
        admin.setFirstName("Studio");
        admin.setLastName("Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Student.Role.ADMIN);
        admin.setMemberSince(LocalDate.now());

        studentRepository.save(admin);
        log.info("Admin account created: {}", adminEmail);
    }
}
package com.yogastudio.config;

import com.yogastudio.entity.Student;
import com.yogastudio.repository.StudentRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;

    public StudentUserDetailsService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));

        if (student.getPassword() == null) {
            throw new UsernameNotFoundException("User has no credentials: " + email);
        }

        return new User(
                student.getEmail(),
                student.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + student.getRole().name()))
        );
    }
}
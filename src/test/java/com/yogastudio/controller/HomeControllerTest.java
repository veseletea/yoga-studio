package com.yogastudio.controller;

import com.yogastudio.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class) // verificam si ca endpoint-ul e public (fara 401)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void welcomeIsPublicAndReturnsAppInfo() throws Exception {
        mockMvc.perform(get("/api"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.appName").value("Yoga Studio Management"))
                .andExpect(jsonPath("$.version").value("1.0"))
                .andExpect(jsonPath("$.status").value("Running"))
                .andExpect(jsonPath("$.availableEndpoints", hasItem("/api/classes")))
                .andExpect(jsonPath("$.liveSince").value("2026"));
    }
}
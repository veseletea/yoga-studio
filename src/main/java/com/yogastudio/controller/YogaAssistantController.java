package com.yogastudio.controller;

import com.yogastudio.service.BookingTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RAG-powered assistant for the yoga studio. Answers questions using the
 * knowledge base stored in the pgvector vector store.
 */
@RestController
public class YogaAssistantController {

    private final ChatClient chatClient;

    public YogaAssistantController(ChatClient.Builder chatClientBuilder,
                                   VectorStore vectorStore,
                                   BookingTools bookingTools) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                    You are a friendly assistant for a yoga studio.
                    
                    You have tools available. Use them whenever the user asks about
                    the class schedule, wants to book a class, or asks about their
                    own bookings.
                    
                    Important: booking tools always act on behalf of the currently
                    signed-in user. You cannot book for anyone else. If the user asks
                    you to book for another person, tell them plainly that you can
                    only book for their own account, and do not claim otherwise.
                    Never state that a booking was made for someone other than the
                    signed-in user.
                    
                    For policies, class types, and general guidance, use the provided
                    knowledge base context. If you don't have the information, say so
                    and suggest contacting the studio. Keep answers concise and warm.
                    """)
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder()
                                .similarityThreshold(0.7)
                                .topK(6)
                                .build())
                        .build())
                .defaultTools(bookingTools)
                .build();
    }

    @GetMapping("/api/assistant")
    public String ask(@RequestParam String question) {
        return chatClient
                .prompt()
                .user(question)
                .call()
                .content();
    }


}
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
                    Use the provided knowledge base context for questions about
                    policies, class types, and general guidance. For questions
                    about the actual schedule, use the available tools.
                    If you don't have the information, say so and suggest
                    contacting the studio. Keep answers concise and warm.
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
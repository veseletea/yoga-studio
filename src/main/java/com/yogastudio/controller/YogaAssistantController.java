package com.yogastudio.controller;

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

    public YogaAssistantController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are a friendly assistant for a yoga studio.
                        Answer the user's question using only the provided context
                        from the studio's knowledge base. If the answer is not in
                        the context, say you don't have that information and suggest
                        contacting the studio. Keep answers concise and warm.
                        """)
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder()
                                .similarityThreshold(0.3)
                                .topK(6)
                                .build())
                        .build())
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
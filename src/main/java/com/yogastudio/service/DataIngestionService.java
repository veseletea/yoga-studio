package com.yogastudio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Loads the yoga studio knowledge base (markdown files) into the pgvector store.
 * Runs once at startup: reads each document, splits it into chunks, and stores
 * the chunks as embeddings. Skips ingestion if the store is already populated,
 * so restarts do not create duplicate entries.
 */
@Component
public class DataIngestionService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataIngestionService.class);

    private static final String DOCUMENTS_LOCATION = "classpath:documents/*.md";

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;
    private final ResourcePatternResolver resourcePatternResolver;

    public DataIngestionService(VectorStore vectorStore, JdbcTemplate jdbcTemplate) {
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        Integer existingCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM vector_store", Integer.class);

        if (existingCount != null && existingCount > 0) {
            log.info("Vector store already contains {} chunks. Skipping ingestion.", existingCount);
            return;
        }

        log.info("Vector store is empty. Starting knowledge base ingestion...");
        ingest();
    }

    private void ingest() throws IOException {
        Resource[] documents = resourcePatternResolver.getResources(DOCUMENTS_LOCATION);
        TokenTextSplitter splitter = new TokenTextSplitter();
        int totalChunks = 0;

        for (Resource document : documents) {
            String filename = document.getFilename();
            log.info("Reading document: {}", filename);

            TextReader reader = new TextReader(document);
            reader.setCharset(StandardCharsets.UTF_8);
            reader.getCustomMetadata().put("source", filename);

            // Remove null bytes (0x00) that PostgreSQL rejects for text columns.
            List<Document> cleanedDocuments = reader.get().stream()
                    .map(doc -> new Document(
                            doc.getText().replace("\u0000", ""),
                            doc.getMetadata()))
                    .toList();

            List<Document> chunks = splitter.apply(cleanedDocuments);
            vectorStore.add(chunks);

            totalChunks += chunks.size();
            log.info("Ingested {} chunks from {}", chunks.size(), filename);
        }

        log.info("Ingestion complete. {} chunks added to the vector store.", totalChunks);
    }
}
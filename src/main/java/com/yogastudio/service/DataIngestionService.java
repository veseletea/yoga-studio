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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;

/**
 * Loads the yoga studio knowledge base (markdown files) into the pgvector store.
 * Runs once at startup and re-ingests automatically whenever the documents
 * change: it fingerprints the knowledge base (filenames + content) and compares
 * it with the fingerprint stored from the previous ingestion. If the fingerprint
 * is unchanged the store is left as-is; if it changed (or was never ingested) the
 * store is cleared and rebuilt, so document edits take effect on the next restart
 * without any manual database cleanup.
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
        Resource[] documents = resourcePatternResolver.getResources(DOCUMENTS_LOCATION);
        String currentFingerprint = computeFingerprint(documents);
        String storedFingerprint = readStoredFingerprint();

        if (currentFingerprint.equals(storedFingerprint)) {
            log.info("Knowledge base unchanged (fingerprint {}). Skipping ingestion.",
                    shortFingerprint(currentFingerprint));
            return;
        }

        if (storedFingerprint == null) {
            log.info("No stored fingerprint. Rebuilding vector store from the knowledge base...");
        } else {
            log.info("Knowledge base changed ({} -> {}). Rebuilding vector store...",
                    shortFingerprint(storedFingerprint), shortFingerprint(currentFingerprint));
        }

        // Always start from a clean store so a rebuild never duplicates chunks,
        // including the first run after this fingerprinting logic is introduced
        // (when the store may already be populated but no fingerprint exists yet).
        jdbcTemplate.execute("TRUNCATE TABLE vector_store");
        ingest(documents);
        saveFingerprint(currentFingerprint);
    }

    private void ingest(Resource[] documents) throws IOException {
        // chunkSize=300 tokens, minChunkSizeChars=100, minChunkLengthToEmbed=5,
        // maxNumChunks=10000, keepSeparator=true
        TokenTextSplitter splitter = new TokenTextSplitter(300, 100, 5, 10000, true);
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
                            doc.getText().replace(Character.toString(0), ""),
                            doc.getMetadata()))
                    .toList();

            List<Document> chunks = splitter.apply(cleanedDocuments);
            vectorStore.add(chunks);

            totalChunks += chunks.size();
            log.info("Ingested {} chunks from {}", chunks.size(), filename);
        }

        log.info("Ingestion complete. {} chunks added to the vector store.", totalChunks);
    }

    /**
     * Fingerprints the knowledge base as a SHA-256 over each document's filename
     * and raw bytes, in a stable (filename-sorted) order, so any added, removed,
     * renamed, or edited document produces a different fingerprint.
     */
    private String computeFingerprint(Resource[] documents) throws IOException {
        List<Resource> sorted = Arrays.stream(documents)
                .sorted(Comparator.comparing(r -> r.getFilename() == null ? "" : r.getFilename()))
                .toList();

        MessageDigest digest = newSha256();
        for (Resource document : sorted) {
            digest.update((document.getFilename() + ":").getBytes(StandardCharsets.UTF_8));
            digest.update(document.getContentAsByteArray());
            digest.update((byte) '\n');
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private String readStoredFingerprint() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS ingestion_state (
                    id INT PRIMARY KEY,
                    fingerprint VARCHAR(64) NOT NULL,
                    updated_at TIMESTAMP NOT NULL DEFAULT now()
                )
                """);
        return jdbcTemplate.query(
                "SELECT fingerprint FROM ingestion_state WHERE id = 1",
                rs -> rs.next() ? rs.getString("fingerprint") : null);
    }

    private void saveFingerprint(String fingerprint) {
        jdbcTemplate.update("""
                INSERT INTO ingestion_state (id, fingerprint, updated_at)
                VALUES (1, ?, now())
                ON CONFLICT (id) DO UPDATE
                    SET fingerprint = EXCLUDED.fingerprint, updated_at = now()
                """, fingerprint);
    }

    private MessageDigest newSha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is required but not available", e);
        }
    }

    private String shortFingerprint(String fingerprint) {
        return fingerprint == null ? "none" : fingerprint.substring(0, Math.min(8, fingerprint.length()));
    }
}

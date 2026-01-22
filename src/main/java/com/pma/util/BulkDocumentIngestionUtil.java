package com.pma.util;

import com.pma.dao.IDocumentRepository;
import com.pma.entities.Document;
import com.pma.services.ingestion.IngestionServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utility to bulk ingest all existing documents from the database
 * into the external ingestion service.
 * 
 * This is useful for:
 * - Initial data migration when setting up the ingestion service
 * - Re-indexing all documents after ingestion service updates
 * - Recovering from ingestion service failures
 * 
 * This utility is DISABLED by default. To enable it, set the following
 * property in application.properties or as a command-line argument:
 * 
 * bulk.ingestion.enabled=true
 * 
 * Or via command line:
 * mvn spring-boot:run
 * -Dspring-boot.run.arguments="--bulk.ingestion.enabled=true"
 */
@Component
@ConditionalOnProperty(name = "bulk.ingestion.enabled", havingValue = "true", matchIfMissing = false)
public class BulkDocumentIngestionUtil implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BulkDocumentIngestionUtil.class);

    @Autowired
    private IDocumentRepository documentRepository;

    @Autowired
    private IngestionServiceClient ingestionServiceClient;

    @Override
    public void run(String... args) throws Exception {
        logger.info("========================================");
        logger.info("Bulk Document Ingestion Utility");
        logger.info("========================================");

        try {
            // Fetch all documents from the database
            logger.info("Fetching all documents from database...");
            List<Document> documents = documentRepository.findAll();

            if (documents.isEmpty()) {
                logger.warn("No documents found in the database. Nothing to ingest.");
                return;
            }

            logger.info("Found {} document(s) to ingest", documents.size());
            logger.info("----------------------------------------");

            int successCount = 0;
            int errorCount = 0;

            // Ingest each document
            for (int i = 0; i < documents.size(); i++) {
                Document doc = documents.get(i);

                try {
                    logger.info("[{}/{}] Ingesting document: ID={}, Title='{}'",
                            i + 1, documents.size(), doc.getId(), doc.getTitle());

                    // Call the ingestion service
                    // Note: Since the service client uses @Async, we need to give it time to
                    // process
                    ingestionServiceClient.ingestDocument(
                            doc.getId(),
                            doc.getTitle(),
                            doc.getContent());

                    successCount++;

                    // Small delay between requests to avoid overwhelming the ingestion service
                    if (i < documents.size() - 1) {
                        Thread.sleep(200); // 200ms delay
                    }

                } catch (Exception e) {
                    logger.error("Failed to ingest document ID={}: {}", doc.getId(), e.getMessage());
                    errorCount++;
                }
            }

            // Wait a bit for async operations to complete before showing summary
            logger.info("Waiting for async operations to complete...");
            Thread.sleep(2000);

            // Print summary
            logger.info("========================================");
            logger.info("Ingestion Summary:");
            logger.info("  Total documents: {}", documents.size());
            logger.info("  Successfully queued: {}", successCount);
            logger.info("  Errors: {}", errorCount);
            logger.info("========================================");
            logger.info("Note: Check ingestion service logs to verify all documents were processed.");
            logger.info("Bulk ingestion utility completed.");

        } catch (Exception e) {
            logger.error("Error during bulk ingestion: {}", e.getMessage(), e);
            throw e;
        }
    }
}

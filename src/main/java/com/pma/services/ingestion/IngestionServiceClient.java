package com.pma.services.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service client for communicating with the external document ingestion
 * service.
 * All operations are asynchronous to avoid blocking document CRUD operations.
 */
@Service
public class IngestionServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(IngestionServiceClient.class);

    @Value("${ingestion.service.url}")
    private String ingestionServiceUrl;

    private final RestTemplate restTemplate;

    public IngestionServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Ingest a new document into the ingestion service.
     * This is called asynchronously and will not block the caller.
     *
     * @param documentId Unique identifier for the document
     * @param title      Document title
     * @param content    Document content
     */
    @Async
    public void ingestDocument(String documentId, String title, String content) {
        try {
            logger.info("Ingesting document with ID: {} to ingestion service", documentId);

            IngestionDocumentDTO doc = new IngestionDocumentDTO(documentId, title, content);
            IngestionRequestDTO request = new IngestionRequestDTO(doc);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<IngestionRequestDTO> entity = new HttpEntity<>(request, headers);

            String url = ingestionServiceUrl + "/documents/ingest";
            restTemplate.postForObject(url, entity, String.class);

            logger.info("Successfully ingested document with ID: {}", documentId);
        } catch (Exception e) {
            logger.error("Failed to ingest document with ID: {}. Error: {}", documentId, e.getMessage());
            // Don't throw - we want to fail gracefully
        }
    }

    /**
     * Delete a document from the ingestion service.
     * This is called asynchronously and will not block the caller.
     *
     * @param documentId Unique identifier for the document to delete
     */
    @Async
    public void deleteDocument(String documentId) {
        try {
            logger.info("Deleting document with ID: {} from ingestion service", documentId);

            String url = ingestionServiceUrl + "/documents/" + documentId;
            restTemplate.delete(url);

            logger.info("Successfully deleted document with ID: {} from ingestion service", documentId);
        } catch (Exception e) {
            logger.error("Failed to delete document with ID: {} from ingestion service. Error: {}",
                    documentId, e.getMessage());
            // Don't throw - we want to fail gracefully
        }
    }

    /**
     * Update a document in the ingestion service.
     * Per the API specification, this requires deleting the old version and
     * re-ingesting.
     * This is called asynchronously and will not block the caller.
     *
     * @param documentId Unique identifier for the document
     * @param title      Updated document title
     * @param content    Updated document content
     */
    @Async
    public void updateDocument(String documentId, String title, String content) {
        try {
            logger.info("Updating document with ID: {} in ingestion service", documentId);

            // Step 1: Delete existing document
            deleteDocument(documentId);

            // Step 2: Re-ingest with updated content
            // Note: We need to wait a bit for the delete to complete
            Thread.sleep(100); // Small delay to ensure delete completes

            ingestDocument(documentId, title, content);

            logger.info("Successfully updated document with ID: {} in ingestion service", documentId);
        } catch (Exception e) {
            logger.error("Failed to update document with ID: {} in ingestion service. Error: {}",
                    documentId, e.getMessage());
            // Don't throw - we want to fail gracefully
        }
    }
}

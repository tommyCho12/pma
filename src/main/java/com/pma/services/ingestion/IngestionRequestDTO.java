package com.pma.services.ingestion;

/**
 * DTO representing the complete request payload for the ingestion service API.
 * Wraps the IngestionDocumentDTO in a "document" field.
 */
public class IngestionRequestDTO {
    private IngestionDocumentDTO document;

    public IngestionRequestDTO() {
    }

    public IngestionRequestDTO(IngestionDocumentDTO document) {
        this.document = document;
    }

    public IngestionDocumentDTO getDocument() {
        return document;
    }

    public void setDocument(IngestionDocumentDTO document) {
        this.document = document;
    }
}

package com.pma.services.ingestion;

/**
 * DTO representing a document for the ingestion service API.
 * This is the nested structure within the request payload.
 */
public class IngestionDocumentDTO {
    private String document_id;
    private String title;
    private String content;

    public IngestionDocumentDTO() {
    }

    public IngestionDocumentDTO(String documentId, String title, String content) {
        this.document_id = documentId;
        this.title = title;
        this.content = content;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

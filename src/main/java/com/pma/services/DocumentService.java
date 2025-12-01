package com.pma.services;

import com.pma.dao.IDocumentRepository;
import com.pma.entities.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    @Autowired
    private IDocumentRepository repository;

    @Autowired
    private DocumentIdGenerator idGenerator;

    public List<Document> findAll() {
        return repository.findAll();
    }

    public Optional<Document> findById(String id) {
        return repository.findById(id);
    }

    public Document save(Document document) {
        // If creating new document (no ID), generate custom ID
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(idGenerator.generateId());
            document.setCreatedDate(java.time.LocalDateTime.now());
        }
        document.setUpdatedDate(java.time.LocalDateTime.now());
        return repository.save(document);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public List<Document> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.searchDocuments(keyword);
    }

    public Document update(String id, Document updatedDocument) {
        Optional<Document> existingDoc = repository.findById(id);
        if (existingDoc.isEmpty()) {
            return null; // Document not found
        }

        // Preserve the ID and creation date
        updatedDocument.setId(id);
        updatedDocument.setCreatedDate(existingDoc.get().getCreatedDate());
        updatedDocument.setUpdatedDate(java.time.LocalDateTime.now());

        return repository.save(updatedDocument);
    }

    public Document markAsReviewed(String id, String reviewedBy) {
        Optional<Document> existingDoc = repository.findById(id);
        if (existingDoc.isEmpty()) {
            return null; // Document not found
        }

        Document document = existingDoc.get();
        document.setReviewer(reviewedBy);
        document.setReviewedDate(java.time.LocalDateTime.now());
        document.setUpdatedDate(java.time.LocalDateTime.now());

        return repository.save(document);
    }
}
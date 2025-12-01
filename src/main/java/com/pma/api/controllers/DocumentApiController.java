package com.pma.api.controllers;

import com.pma.entities.Document;
import com.pma.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
public class DocumentApiController {

    @Autowired
    private DocumentService documentService;

    // Create a new document
    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document) {
        Document savedDocument = documentService.save(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDocument);
    }

    // Get document by ID
    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable String id) {
        Optional<Document> document = documentService.findById(id);
        return document.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all documents
    @GetMapping("/all")
    public List<Document> getAllDocuments() {
        return documentService.findAll();
    }

    // Update document
    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable String id, @RequestBody Document document) {
        Document updatedDocument = documentService.update(id, document);
        if (updatedDocument == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedDocument);
    }

    // Mark document as reviewed
    @PatchMapping("/{id}/review")
    public ResponseEntity<Document> markAsReviewed(@PathVariable String id,
            @RequestBody Map<String, String> reviewData) {
        String reviewedBy = reviewData.get("reviewedBy");
        if (reviewedBy == null || reviewedBy.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Document reviewedDocument = documentService.markAsReviewed(id, reviewedBy);
        if (reviewedDocument == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reviewedDocument);
    }

    // Search documents (optional - client-side search is faster for small datasets)
    @GetMapping("/search")
    public List<Document> searchDocuments(@RequestParam String q) {
        return documentService.search(q);
    }

    // Delete document
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        documentService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
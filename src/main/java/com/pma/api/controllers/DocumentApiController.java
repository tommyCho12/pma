package com.pma.api.controllers;

import com.pma.entities.Document;
import com.pma.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
public class DocumentApiController {

    @Autowired
    private DocumentService documentService;

    // Create a new document
    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document) {
        // Get the authenticated user and set as author
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            String username = authentication.getName();
            document.setAuthor(username);
        }

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
    public ResponseEntity<Document> markAsReviewed(@PathVariable String id) {
        // Get the authenticated user from Security context
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Get the UserDetails from authentication
        org.springframework.security.core.userdetails.UserDetails userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication
                .getPrincipal();
        String username = userDetails.getUsername();

        // Check if user has REVIEWER permission
        boolean hasReviewerPermission = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("PERMISSION_REVIEWER"));

        if (!hasReviewerPermission) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Mark document as reviewed with the authenticated user's username
        Document reviewedDocument = documentService.markAsReviewed(id, username);
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
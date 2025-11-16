package com.pma.api.controllers;

import com.pma.entities.Document;
import com.pma.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentApiController {

    @Autowired
    private DocumentService documentService;

    // Get all documents
    @GetMapping("/all")
    public List<Document> getAllDocuments() {
        return documentService.findAll();
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
package com.pma.controllers;

import com.pma.dao.IDocumentRepository;
import com.pma.entities.Document;
import com.pma.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Controller
@RequestMapping("/documents")
public class DocumentController {
    @Autowired
    DocumentService documentService;

    @Autowired
    IDocumentRepository documentRepository;

    @GetMapping
    public String display(Model model) {
        Iterable<Document> documents = documentService.findAll();
        model.addAttribute("documentList", documents);

        // Check if user is authenticated for conditional button rendering in JS
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser");
        model.addAttribute("isAuthenticated", isAuthenticated);

        return "documents/documents-home";
    }

    @GetMapping("/view/{id}")
    public String viewDocument(@PathVariable("id") String id, Model model) {
        Document document = documentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        // Get current authenticated user and check if they have REVIEWER permission
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        boolean isReviewer = false;
        if (authentication != null && authentication.isAuthenticated()) {
            isReviewer = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("PERMISSION_REVIEWER"));
        }

        model.addAttribute("document", document);
        model.addAttribute("isReviewer", isReviewer);
        return "documents/document-view";
    }

    @GetMapping("/new")
    public String displayDocumentForm(Model model) {
        Document document = new Document();
        model.addAttribute("document", document);
        return "documents/new-document";
    }

    @GetMapping("/update/{id}")
    public String updateDocument(@PathVariable String id, Model model) {
        Document document = documentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        model.addAttribute("document", document);
        return "documents/new-document";
    }

    @GetMapping("/delete/{id}")
    public String deleteDocument(@PathVariable String id) {
        documentService.deleteById(id);
        return "redirect:/documents";
    }

    @PostMapping("/save")
    public String createDocument(Model model, @Valid Document document, Errors errors) {
        if (errors.hasErrors())
            return "documents/new-document";

        // Get the authenticated user and set as author
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            String username = authentication.getName();

            // Only set author if it's a new document (id is null or empty)
            if (document.getId() == null || document.getId().isEmpty()) {
                document.setAuthor(username);
            }
        }

        documentService.save(document);
        return "redirect:/documents"; // redirect to prevent multiple saves.
    }
}

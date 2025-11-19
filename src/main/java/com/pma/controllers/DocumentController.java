package com.pma.controllers;

import com.pma.dao.IDocumentRepository;
import com.pma.entities.Document;
import com.pma.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/documents")
public class DocumentController
{
    @Autowired
    DocumentService documentService;

    @Autowired
    IDocumentRepository documentRepository;

    @Autowired
    private RestTemplateAutoConfiguration restTemplateAutoConfiguration;

    @GetMapping
    public String display(Model model){
        Iterable<Document> documents = documentService.findAll();
        model.addAttribute("documentList", documents);

        return "documents/documents-home";
    }

    @GetMapping("/view/{id}")
    public String viewDocument(@PathVariable("id") String id, Model model) {
        Document document = documentService.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        model.addAttribute("document", document);
        return "documents/document-view";
    }

    @GetMapping("/new")
    public String displayDocumentForm(Model model){
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
    public String createDocument(Model model, @Valid Document document, Errors errors){
        if(errors.hasErrors())
            return "documents/new-document";

        documentService.save(document);
        return "redirect:/documents"; //redirect to prevent multiple saves.
    }
}

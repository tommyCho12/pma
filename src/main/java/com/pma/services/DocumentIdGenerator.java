package com.pma.services;

import com.pma.dao.IDocumentRepository;
import com.pma.entities.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DocumentIdGenerator {

    @Autowired
    private IDocumentRepository documentRepository;

    /**
     * Generate ID in format: DOC-YYYYMMDD-XXX
     * Example: DOC-20250116-001
     */
    public String generateId() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String idPrefix = "DOC-" + datePrefix + "-";

        // Find the last document created today
        String lastId = findLastIdWithPrefix(idPrefix);

        int nextSequence = 1;
        if (lastId != null) {
            // Extract sequence number from last ID
            String sequencePart = lastId.substring(lastId.lastIndexOf("-") + 1);
            nextSequence = Integer.parseInt(sequencePart) + 1;
        }

        // Format: DOC-20250116-001
        return String.format("%s%03d", idPrefix, nextSequence);
    }

    /**
     * Alternative: Simple sequential format DOC-00001
     */
    public String generateSimpleSequentialId() {
        long count = documentRepository.count();
        return String.format("DOC-%05d", count + 1);
    }

    private String findLastIdWithPrefix(String prefix) {
        List<Document> documents = documentRepository.findByIdStartingWithOrderByIdDesc(prefix);
        return documents.isEmpty() ? null : documents.get(0).getId();
    }
}
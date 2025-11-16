package com.pma.dao;

import com.pma.entities.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IDocumentRepository extends MongoRepository<Document, String>
{
    // Spring Data MongoDB will auto-implement basic CRUD operations

    // You can add custom queries if needed:
    @Query("{ 'content': { $regex: ?0, $options: 'i' } }")
    Document findByContent(String content);

    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    Document findByTitle(String title);

    // Search in title and content (case-insensitive)
    @Query("{ $or: [ " +
        "{ 'title': { $regex: ?0, $options: 'i' } }, " +
        "{ 'content': { $regex: ?0, $options: 'i' } } " +
        "] }")
    List<Document> searchDocuments(String keyword);

    // Find documents with ID starting with prefix, sorted descending
    List<Document> findByIdStartingWithOrderByIdDesc(String prefix);
}

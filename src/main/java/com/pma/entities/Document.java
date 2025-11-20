package com.pma.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@org.springframework.data.mongodb.core.mapping.Document(collection = "documentsCollection")
public class Document
{
    @Id
    private String id;

    private String title;
    private String content;

    private String author;
    private String reviewer;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime reviewedDate;

    private List<String> tags;

    // Constructors
    public Document() {
    }

    public Document(String id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getReviewer()
    {
        return reviewer;
    }

    public void setReviewer(String reviewer)
    {
        this.reviewer = reviewer;
    }

    public LocalDateTime getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate)
    {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate()
    {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate)
    {
        this.updatedDate = updatedDate;
    }

    public LocalDateTime getReviewedDate()
    {
        return reviewedDate;
    }

    public void setReviewedDate(LocalDateTime reviewedDate)
    {
        this.reviewedDate = reviewedDate;
    }

    public List<String> getTags()
    {
        return tags;
    }

    public void setTags(List<String> tags)
    {
        this.tags = tags;
    }

    // Optional: toString, equals, hashCode
    @Override
    public String toString() {
        return "Document{" +
            "id='" + id + '\'' +
            ", content='" + content + '\'' +
            '}';
    }
}


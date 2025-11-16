package com.pma.entities;

import javax.persistence.*;


@org.springframework.data.mongodb.core.mapping.Document(collection = "documentsCollection")  // Replace with your actual collection name
public class Document
{

    @Id
    private String id;

    private String title;
    private String content;

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

    // Optional: toString, equals, hashCode
    @Override
    public String toString() {
        return "Document{" +
            "id='" + id + '\'' +
            ", content='" + content + '\'' +
            '}';
    }
}


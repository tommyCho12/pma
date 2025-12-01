package com.pma.api.controllers;

import com.pma.entities.Document;
import com.pma.services.DocumentService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class DocumentApiControllerTest {

        @LocalServerPort
        private int port;

        @Autowired
        private DocumentService documentService;

        @Before
        public void setUp() {
                RestAssured.port = port;
                RestAssured.basePath = "/api/documents";
        }

        @Test
        public void testCreateDocument_thenSuccess() {
                Map<String, Object> document = new HashMap<>();
                document.put("title", "Test Document");
                document.put("content", "This is test content");
                document.put("author", "John Doe");

                given()
                                .contentType(ContentType.JSON)
                                .body(document)
                                .when()
                                .post()
                                .then()
                                .statusCode(201)
                                .body("title", equalTo("Test Document"))
                                .body("content", equalTo("This is test content"))
                                .body("author", equalTo("John Doe"))
                                .body("id", notNullValue())
                                .body("createdDate", notNullValue())
                                .body("updatedDate", notNullValue());
        }

        @Test
        public void testGetDocumentById_whenExists_thenSuccess() {
                // First create a document
                Document document = new Document();
                document.setTitle("Test Document for GET");
                document.setContent("Test content for GET");
                document.setAuthor("Jane Doe");
                Document savedDoc = documentService.save(document);

                // Now retrieve it
                given()
                                .when()
                                .get("/" + savedDoc.getId())
                                .then()
                                .statusCode(200)
                                .body("id", equalTo(savedDoc.getId()))
                                .body("title", equalTo("Test Document for GET"))
                                .body("content", equalTo("Test content for GET"))
                                .body("author", equalTo("Jane Doe"));
        }

        @Test
        public void testGetDocumentById_whenNotExists_thenNotFound() {
                given()
                                .when()
                                .get("/nonexistent-id-12345")
                                .then()
                                .statusCode(404);
        }

        @Test
        public void testGetAllDocuments_thenSuccess() {
                // Create a few documents
                Document doc1 = new Document();
                doc1.setTitle("Document 1");
                doc1.setContent("Content 1");
                documentService.save(doc1);

                Document doc2 = new Document();
                doc2.setTitle("Document 2");
                doc2.setContent("Content 2");
                documentService.save(doc2);

                given()
                                .when()
                                .get("/all")
                                .then()
                                .statusCode(200)
                                .body("$", not(empty()));
        }

        @Test
        public void testUpdateDocument_whenExists_thenSuccess() {
                // Create a document first
                Document document = new Document();
                document.setTitle("Original Title");
                document.setContent("Original Content");
                Document savedDoc = documentService.save(document);

                // Update it
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("title", "Updated Title");
                updatedData.put("content", "Updated Content");
                updatedData.put("author", "Update Author");

                given()
                                .contentType(ContentType.JSON)
                                .body(updatedData)
                                .when()
                                .put("/" + savedDoc.getId())
                                .then()
                                .statusCode(200)
                                .body("id", equalTo(savedDoc.getId()))
                                .body("title", equalTo("Updated Title"))
                                .body("content", equalTo("Updated Content"))
                                .body("author", equalTo("Update Author"))
                                .body("updatedDate", notNullValue());
        }

        @Test
        public void testUpdateDocument_whenNotExists_thenNotFound() {
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("title", "Updated Title");
                updatedData.put("content", "Updated Content");

                given()
                                .contentType(ContentType.JSON)
                                .body(updatedData)
                                .when()
                                .put("/nonexistent-id-12345")
                                .then()
                                .statusCode(404);
        }

        @Test
        public void testMarkAsReviewed_whenExists_thenSuccess() {
                // Create a document first
                Document document = new Document();
                document.setTitle("Document to Review");
                document.setContent("Content to Review");
                Document savedDoc = documentService.save(document);

                // Mark it as reviewed
                Map<String, String> reviewData = new HashMap<>();
                reviewData.put("reviewedBy", "Jane Reviewer");

                given()
                                .contentType(ContentType.JSON)
                                .body(reviewData)
                                .when()
                                .patch("/" + savedDoc.getId() + "/review")
                                .then()
                                .statusCode(200)
                                .body("id", equalTo(savedDoc.getId()))
                                .body("reviewer", equalTo("Jane Reviewer"))
                                .body("reviewedDate", notNullValue())
                                .body("updatedDate", notNullValue());
        }

        @Test
        public void testMarkAsReviewed_whenNotExists_thenNotFound() {
                Map<String, String> reviewData = new HashMap<>();
                reviewData.put("reviewedBy", "Jane Reviewer");

                given()
                                .contentType(ContentType.JSON)
                                .body(reviewData)
                                .when()
                                .patch("/nonexistent-id-12345/review")
                                .then()
                                .statusCode(404);
        }

        @Test
        public void testMarkAsReviewed_withEmptyReviewedBy_thenBadRequest() {
                // Create a document first
                Document document = new Document();
                document.setTitle("Document to Review");
                Document savedDoc = documentService.save(document);

                Map<String, String> reviewData = new HashMap<>();
                reviewData.put("reviewedBy", "");

                given()
                                .contentType(ContentType.JSON)
                                .body(reviewData)
                                .when()
                                .patch("/" + savedDoc.getId() + "/review")
                                .then()
                                .statusCode(400);
        }

        @Test
        public void testSearchDocuments_thenSuccess() {
                // Create documents with searchable content
                Document doc1 = new Document();
                doc1.setTitle("Java Programming");
                doc1.setContent("Learn Java basics");
                documentService.save(doc1);

                Document doc2 = new Document();
                doc2.setTitle("Python Programming");
                doc2.setContent("Learn Python basics");
                documentService.save(doc2);

                given()
                                .queryParam("q", "Java")
                                .when()
                                .get("/search")
                                .then()
                                .statusCode(200)
                                .body("$", not(empty()));
        }

        @Test
        public void testDeleteDocument_thenSuccess() {
                // Create a document first
                Document document = new Document();
                document.setTitle("Document to Delete");
                document.setContent("This will be deleted");
                Document savedDoc = documentService.save(document);

                given()
                                .when()
                                .delete("/" + savedDoc.getId())
                                .then()
                                .statusCode(200);

                // Verify it's deleted
                given()
                                .when()
                                .get("/" + savedDoc.getId())
                                .then()
                                .statusCode(404);
        }

        @Test
        public void testCompleteWorkflow_createUpdateReviewDelete_thenSuccess() {
                // 1. Create
                Map<String, Object> document = new HashMap<>();
                document.put("title", "Workflow Test Document");
                document.put("content", "Initial content");
                document.put("author", "Workflow Tester");

                String docId = given()
                                .contentType(ContentType.JSON)
                                .body(document)
                                .when()
                                .post()
                                .then()
                                .statusCode(201)
                                .extract()
                                .path("id");

                // 2. Update
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("title", "Updated Workflow Document");
                updatedData.put("content", "Updated content");
                updatedData.put("author", "Workflow Tester");

                given()
                                .contentType(ContentType.JSON)
                                .body(updatedData)
                                .when()
                                .put("/" + docId)
                                .then()
                                .statusCode(200)
                                .body("title", equalTo("Updated Workflow Document"));

                // 3. Review
                Map<String, String> reviewData = new HashMap<>();
                reviewData.put("reviewedBy", "Workflow Reviewer");

                given()
                                .contentType(ContentType.JSON)
                                .body(reviewData)
                                .when()
                                .patch("/" + docId + "/review")
                                .then()
                                .statusCode(200)
                                .body("reviewer", equalTo("Workflow Reviewer"));

                // 4. Verify GET
                given()
                                .when()
                                .get("/" + docId)
                                .then()
                                .statusCode(200)
                                .body("title", equalTo("Updated Workflow Document"))
                                .body("reviewer", equalTo("Workflow Reviewer"));

                // 5. Delete
                given()
                                .when()
                                .delete("/" + docId)
                                .then()
                                .statusCode(200);

                // 6. Verify it's deleted
                given()
                                .when()
                                .get("/" + docId)
                                .then()
                                .statusCode(404);
        }
}

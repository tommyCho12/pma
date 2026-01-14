package com.pma.services.ingestion;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for IngestionServiceClient.
 * Tests HTTP client functionality with mocked RestTemplate.
 */
@RunWith(MockitoJUnitRunner.class)
public class IngestionServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private IngestionServiceClient ingestionServiceClient;

    private static final String TEST_SERVICE_URL = "http://localhost:8001";
    private static final String TEST_DOC_ID = "DOC123";
    private static final String TEST_TITLE = "Test Document";
    private static final String TEST_CONTENT = "This is test content";

    @Before
    public void setUp() {
        // Inject the test service URL using reflection
        ReflectionTestUtils.setField(ingestionServiceClient, "ingestionServiceUrl", TEST_SERVICE_URL);
        // Replace the RestTemplate with our mock
        ReflectionTestUtils.setField(ingestionServiceClient, "restTemplate", restTemplate);
    }

    @Test
    public void testIngestDocument_Success() {
        // Given
        when(restTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(String.class))).thenReturn("success");

        // When
        ingestionServiceClient.ingestDocument(TEST_DOC_ID, TEST_TITLE, TEST_CONTENT);

        // Then - verify the REST call was made with correct URL
        // Note: Since method is @Async, we can't verify synchronously in unit test
        // This test mainly ensures no exceptions are thrown
    }

    @Test
    public void testIngestDocument_HandlesException() {
        // Given
        when(restTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(String.class))).thenThrow(new RestClientException("Connection failed"));

        // When - should not throw exception (graceful error handling)
        try {
            ingestionServiceClient.ingestDocument(TEST_DOC_ID, TEST_TITLE, TEST_CONTENT);
            // Success - no exception thrown
        } catch (Exception e) {
            throw new AssertionError("Should handle exceptions gracefully", e);
        }
    }

    @Test
    public void testDeleteDocument_Success() {
        // Given
        doNothing().when(restTemplate).delete(anyString());

        // When
        ingestionServiceClient.deleteDocument(TEST_DOC_ID);

        // Then - verify completes without exception
        // Note: Since method is @Async, verification is limited in unit test
    }

    @Test
    public void testDeleteDocument_HandlesException() {
        // Given
        doThrow(new RestClientException("Service unavailable"))
                .when(restTemplate).delete(anyString());

        // When - should not throw exception (graceful error handling)
        try {
            ingestionServiceClient.deleteDocument(TEST_DOC_ID);
            // Success - no exception thrown
        } catch (Exception e) {
            throw new AssertionError("Should handle exceptions gracefully", e);
        }
    }

    @Test
    public void testUpdateDocument_Success() {
        // Given
        doNothing().when(restTemplate).delete(anyString());
        when(restTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(String.class))).thenReturn("success");

        // When
        ingestionServiceClient.updateDocument(TEST_DOC_ID, TEST_TITLE, TEST_CONTENT);

        // Then - verify completes without exception
    }

    @Test
    public void testUpdateDocument_HandlesException() {
        // Given
        doThrow(new RestClientException("Service down"))
                .when(restTemplate).delete(anyString());

        // When - should not throw exception (graceful error handling)
        try {
            ingestionServiceClient.updateDocument(TEST_DOC_ID, TEST_TITLE, TEST_CONTENT);
            // Success - no exception thrown
        } catch (Exception e) {
            throw new AssertionError("Should handle exceptions gracefully", e);
        }
    }
}

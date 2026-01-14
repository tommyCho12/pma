# Message Queue Implementation Plan for Document Ingestion

## Overview
This document outlines the implementation plan for integrating a message queue to handle document ingestion events asynchronously. The message queue will improve reliability, enable retry logic, and decouple document CRUD operations from the ingestion service.

## Technology Analysis

### Available Free Message Queue Solutions

#### 1. **RabbitMQ** ⭐ **RECOMMENDED**

**Pros:**
- Mature, battle-tested solution with extensive Spring Boot integration
- Native Docker support with official images
- Lightweight (runs efficiently in development)
- Excellent Spring AMQP support with auto-configuration
- Management UI included for monitoring
- Great documentation and community support
- Dead Letter Queue (DLQ) support out of the box
- Message persistence and durability guarantees

**Cons:**
- Slightly more complex setup than embedded solutions
- Requires additional container in Docker Compose

**Why Recommended for This Use Case:**
- Perfect fit for event-driven architectures
- Excellent retry and failure handling
- Easy to test and debug with management UI
- Production-ready with minimal configuration

#### 2. **Apache Kafka**

**Pros:**
- High throughput and scalability
- Event streaming capabilities
- Strong durability guarantees
- Spring Kafka integration available

**Cons:**
- Overkill for simple document ingestion (designed for massive scale)
- Higher resource consumption (requires Zookeeper or KRaft)
- More complex configuration and operational overhead
- Steeper learning curve

**Verdict:** Too complex for current requirements

#### 3. **Redis Streams**

**Pros:**
- Ultra-lightweight
- Can also serve as cache
- Simple setup
- Spring Data Redis support

**Cons:**
- Less mature for message queuing compared to RabbitMQ
- Limited message acknowledgment patterns
- No native DLQ support
- Persistence is secondary feature (primarily in-memory)

**Verdict:** Good for caching, but RabbitMQ is better for reliable messaging

#### 4. **ActiveMQ / ActiveMQ Artemis**

**Pros:**
- JMS standard implementation
- Spring JMS support
- Free and open-source

**Cons:**
- Less active community than RabbitMQ
- Heavier resource footprint
- Older technology

**Verdict:** RabbitMQ is more modern and better supported

---

## Recommended Architecture: RabbitMQ

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                      PMA Application                        │
│                                                             │
│  ┌──────────────┐          ┌──────────────────────────┐   │
│  │  Document    │          │  Message Queue Producer  │   │
│  │  Controller  │─────────▶│  (DocumentEventPublisher)│   │
│  │  /Service    │          └──────────┬───────────────┘   │
│  └──────────────┘                     │                    │
└────────────────────────────────────────┼────────────────────┘
                                         │
                                         ▼
                          ┌──────────────────────────┐
                          │      RabbitMQ Broker     │
                          │                          │
                          │  ┌────────────────────┐  │
                          │  │ document.ingestion │  │
                          │  │      Exchange      │  │
                          │  └─────────┬──────────┘  │
                          │            │             │
                          │  ┌─────────▼──────────┐  │
                          │  │ document.ingestion │  │
                          │  │       Queue        │  │
                          │  └─────────┬──────────┘  │
                          │            │             │
                          │  ┌─────────▼──────────┐  │
                          │  │  DLQ (Dead Letter  │  │
                          │  │      Queue)        │  │
                          │  └────────────────────┘  │
                          └──────────────────────────┘
                                         │
                                         ▼
                          ┌──────────────────────────┐
                          │  Message Queue Consumer  │
                          │ (IngestionEventListener) │
                          └──────────┬───────────────┘
                                     │
                                     ▼
                          ┌──────────────────────────┐
                          │   Ingestion Service API  │
                          │   (http://localhost:8001)│
                          └──────────────────────────┘
```

### Event Flow

1. **Document Created/Updated/Deleted** → Controller calls Service
2. **Service** → Publishes event to RabbitMQ exchange
3. **RabbitMQ** → Routes message to ingestion queue
4. **Consumer** → Processes message asynchronously
5. **Consumer** → Calls Ingestion Service API
6. **On Failure** → Message retried (max 3 attempts)
7. **After Max Retries** → Message moved to DLQ for manual review

---

## Implementation Plan

### Phase 1: Setup RabbitMQ Infrastructure

#### 1.1 Update Docker Compose

**File:** `docker-compose-mongodb.yml`

Add RabbitMQ service:

```yaml
rabbitmq:
  image: rabbitmq:3.13-management-alpine
  container_name: rabbitmqContainer
  ports:
    - "5672:5672"   # AMQP port
    - "15672:15672" # Management UI
  environment:
    RABBITMQ_DEFAULT_USER: admin
    RABBITMQ_DEFAULT_PASS: admin
  volumes:
    - rabbitmq-data:/var/lib/rabbitmq
  restart: unless-stopped
  healthcheck:
    test: ["CMD", "rabbitmq-diagnostics", "ping"]
    interval: 10s
    timeout: 5s
    retries: 5
```

Add volume:
```yaml
volumes:
  rabbitmq-data:
```

#### 1.2 Add Maven Dependencies

**File:** `pom.xml`

```xml
<!-- RabbitMQ / AMQP Support -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>

<!-- For testing RabbitMQ -->
<dependency>
    <groupId>org.springframework.amqp</groupId>
    <artifactId>spring-rabbit-test</artifactId>
    <scope>test</scope>
</dependency>
```

#### 1.3 Update Application Configuration

**File:** `src/main/resources/application.properties`

```properties
# RabbitMQ Configuration
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=admin

# Ingestion Service Configuration
ingestion.service.url=http://localhost:8001

# Message Queue Configuration
queue.document.ingestion.name=document.ingestion.queue
queue.document.ingestion.exchange=document.ingestion.exchange
queue.document.ingestion.routing-key=document.ingestion
queue.document.ingestion.dlq.name=document.ingestion.dlq
```

**File:** `src/main/resources/application-test.properties`

```properties
# Use embedded RabbitMQ for tests or disable
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
```

---

### Phase 2: Create Domain Models

#### 2.1 Document Event DTO

**File:** `src/main/java/com/pma/messaging/events/DocumentEvent.java`

```java
package com.pma.messaging.events;

import java.io.Serializable;
import java.time.LocalDateTime;

public class DocumentEvent implements Serializable {
    
    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
    
    private String documentId;
    private String title;
    private String content;
    private EventType eventType;
    private LocalDateTime timestamp;
    
    // Constructors, Getters, Setters, toString()
}
```

---

### Phase 3: Configure RabbitMQ

#### 3.1 RabbitMQ Configuration Class

**File:** `src/main/java/com/pma/messaging/config/RabbitMQConfig.java`

```java
package com.pma.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    @Value("${queue.document.ingestion.name}")
    private String queueName;
    
    @Value("${queue.document.ingestion.exchange}")
    private String exchangeName;
    
    @Value("${queue.document.ingestion.routing-key}")
    private String routingKey;
    
    @Value("${queue.document.ingestion.dlq.name}")
    private String dlqName;
    
    @Bean
    public Queue documentIngestionQueue() {
        return QueueBuilder.durable(queueName)
            .withArgument("x-dead-letter-exchange", "")
            .withArgument("x-dead-letter-routing-key", dlqName)
            .build();
    }
    
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(dlqName).build();
    }
    
    @Bean
    public TopicExchange documentIngestionExchange() {
        return new TopicExchange(exchangeName);
    }
    
    @Bean
    public Binding binding(Queue documentIngestionQueue, 
                          TopicExchange documentIngestionExchange) {
        return BindingBuilder
            .bind(documentIngestionQueue)
            .to(documentIngestionExchange)
            .with(routingKey);
    }
    
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
```

---

### Phase 4: Implement Producer (Publisher)

#### 4.1 Document Event Publisher

**File:** `src/main/java/com/pma/messaging/publisher/DocumentEventPublisher.java`

```java
package com.pma.messaging.publisher;

import com.pma.messaging.events.DocumentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DocumentEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentEventPublisher.class);
    
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${queue.document.ingestion.exchange}")
    private String exchange;
    
    @Value("${queue.document.ingestion.routing-key}")
    private String routingKey;
    
    public DocumentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void publishDocumentEvent(DocumentEvent event) {
        try {
            logger.info("Publishing document event: {} for document ID: {}", 
                       event.getEventType(), event.getDocumentId());
            
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            
            logger.info("Document event published successfully");
        } catch (Exception e) {
            logger.error("Failed to publish document event: {}", e.getMessage(), e);
            // Consider fallback strategy or alert
        }
    }
}
```

---

### Phase 5: Implement Consumer (Listener)

#### 5.1 Ingestion Service Client

**File:** `src/main/java/com/pma/messaging/client/IngestionServiceClient.java`

```java
package com.pma.messaging.client;

import com.pma.messaging.events.DocumentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class IngestionServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(IngestionServiceClient.class);
    
    private final RestTemplate restTemplate;
    
    @Value("${ingestion.service.url}")
    private String ingestionServiceUrl;
    
    public IngestionServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public void ingestDocument(DocumentEvent event) {
        String url = ingestionServiceUrl + "/documents/ingest";
        
        Map<String, Object> payload = new HashMap<>();
        Map<String, String> document = new HashMap<>();
        document.put("document_id", event.getDocumentId());
        document.put("title", event.getTitle());
        document.put("content", event.getContent());
        payload.put("document", document);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        
        logger.info("Ingesting document: {}", event.getDocumentId());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        logger.info("Ingestion response: {}", response.getStatusCode());
    }
    
    public void deleteDocument(String documentId) {
        String url = ingestionServiceUrl + "/documents/" + documentId;
        
        logger.info("Deleting document from ingestion service: {}", documentId);
        restTemplate.delete(url);
        logger.info("Document deleted from ingestion service");
    }
}
```

#### 5.2 Document Event Listener

**File:** `src/main/java/com/pma/messaging/listener/DocumentEventListener.java`

```java
package com.pma.messaging.listener;

import com.pma.messaging.client.IngestionServiceClient;
import com.pma.messaging.events.DocumentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class DocumentEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentEventListener.class);
    
    private final IngestionServiceClient ingestionServiceClient;
    
    public DocumentEventListener(IngestionServiceClient ingestionServiceClient) {
        this.ingestionServiceClient = ingestionServiceClient;
    }
    
    @RabbitListener(queues = "${queue.document.ingestion.name}")
    public void handleDocumentEvent(DocumentEvent event,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            logger.info("Received document event: {} for document ID: {}", 
                       event.getEventType(), event.getDocumentId());
            
            switch (event.getEventType()) {
                case CREATED:
                    ingestionServiceClient.ingestDocument(event);
                    break;
                    
                case UPDATED:
                    // Delete then re-ingest
                    ingestionServiceClient.deleteDocument(event.getDocumentId());
                    ingestionServiceClient.ingestDocument(event);
                    break;
                    
                case DELETED:
                    ingestionServiceClient.deleteDocument(event.getDocumentId());
                    break;
                    
                default:
                    logger.warn("Unknown event type: {}", event.getEventType());
            }
            
            logger.info("Document event processed successfully");
            
        } catch (Exception e) {
            logger.error("Failed to process document event: {}", e.getMessage(), e);
            // Exception will trigger retry mechanism
            throw new RuntimeException("Failed to process document event", e);
        }
    }
}
```

#### 5.3 Retry Configuration

**File:** `src/main/java/com/pma/messaging/config/RabbitMQRetryConfig.java`

```java
package com.pma.messaging.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitMQRetryConfig {
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setRetryTemplate(retryTemplate());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        
        return factory;
    }
    
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        
        // Retry 3 times
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);
        
        // Exponential backoff: 2s, 4s, 8s
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(2000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        
        return retryTemplate;
    }
}
```

---

### Phase 6: Update Document Service

#### 6.1 Integrate Publisher into Document Service

**File:** `src/main/java/com/pma/services/DocumentService.java` (modifications)

```java
@Autowired
private DocumentEventPublisher eventPublisher;

public Document createDocument(Document document) {
    // Existing save logic
    Document saved = documentRepository.save(document);
    
    // Publish event
    DocumentEvent event = new DocumentEvent();
    event.setDocumentId(saved.getId().toString());
    event.setTitle(saved.getTitle());
    event.setContent(saved.getContent());
    event.setEventType(DocumentEvent.EventType.CREATED);
    event.setTimestamp(LocalDateTime.now());
    
    eventPublisher.publishDocumentEvent(event);
    
    return saved;
}

// Similar for update and delete methods
```

---

### Phase 7: Add RestTemplate Bean

**File:** `src/main/java/com/pma/config/AppConfig.java`

```java
package com.pma.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

---

### Phase 8: Testing

#### 8.1 Unit Tests

**File:** `src/test/java/com/pma/messaging/publisher/DocumentEventPublisherTest.java`

```java
@SpringBootTest
class DocumentEventPublisherTest {
    
    @Autowired
    private DocumentEventPublisher publisher;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Test
    void shouldPublishDocumentEvent() {
        DocumentEvent event = new DocumentEvent();
        event.setDocumentId("123");
        event.setTitle("Test");
        event.setContent("Content");
        event.setEventType(DocumentEvent.EventType.CREATED);
        
        publisher.publishDocumentEvent(event);
        
        // Verify message received
    }
}
```

#### 8.2 Integration Tests

Test end-to-end flow with embedded RabbitMQ or TestContainers.

---

## Deployment Steps

1. **Start RabbitMQ:**
   ```bash
   docker-compose -f docker-compose-mongodb.yml up -d rabbitmq
   ```

2. **Access Management UI:**
   - URL: http://localhost:15672
   - Username: `admin`
   - Password: `admin`

3. **Verify Queues Created:**
   - Check "Queues" tab in management UI
   - Should see `document.ingestion.queue` and `document.ingestion.dlq`

4. **Run Application:**
   ```bash
   mvn spring-boot:run
   ```

5. **Test Document Operations:**
   - Create/Update/Delete documents
   - Monitor queue in RabbitMQ UI
   - Check application logs for event processing

---

## Monitoring and Observability

### Key Metrics to Monitor

1. **Queue Depth**: Messages waiting in queue
2. **Message Rate**: Messages published/consumed per second
3. **Consumer Count**: Active consumers
4. **DLQ Depth**: Failed messages requiring attention
5. **Processing Time**: Time to process each message

### Logging Strategy

- Log message publication
- Log message consumption start/end
- Log failures with full stack traces
- Log retry attempts
- Alert on DLQ messages

### Health Checks

Add RabbitMQ health indicator (included with Spring Boot Actuator automatically).

---

## Error Handling Strategy

1. **Transient Errors** (network issues): Retry with exponential backoff (max 3 attempts)
2. **Ingestion Service Down**: Retry, then move to DLQ after max attempts
3. **Invalid Message Format**: Log error, move to DLQ immediately
4. **DLQ Messages**: Manual review and reprocessing workflow

---

## Future Enhancements

1. **Priority Queue**: Urgent documents processed first
2. **Message Batching**: Process multiple documents in one API call
3. **Circuit Breaker**: Stop processing if ingestion service is degraded
4. **Metrics Dashboard**: Grafana + Prometheus for monitoring
5. **Admin UI**: Tool to replay DLQ messages
6. **Event Sourcing**: Keep full history of document events

---

## Cost Analysis

**Free Tier:**
- RabbitMQ: 100% free and open-source
- Docker: Free for development
- Spring AMQP: Free

**Production Considerations:**
- Self-hosted RabbitMQ on cloud VMs
- Or managed services: CloudAMQP (has free tier), AWS MQ, Azure Service Bus

---

## Conclusion

**Recommended Solution:** RabbitMQ with Spring AMQP

**Rationale:**
- Battle-tested and reliable
- Excellent Spring Boot integration
- Free and open-source
- Easy to develop and debug
- Production-ready with proper configuration
- Scales well for document ingestion use case

**Estimated Implementation Time:** 2-3 days

**Benefits:**
- Asynchronous processing improves response times
- Automatic retry on failures
- Dead letter queue for failed messages
- Easy monitoring via management UI
- Decoupled architecture for better maintainability

# Utility Scripts

This directory contains standalone utility scripts for administrative tasks.

## BulkDocumentIngestionUtil

**Purpose**: Bulk ingest all existing documents from the database into the external ingestion service.

### When to Use

- **Initial Setup**: When first setting up the ingestion service and you have existing documents
- **Re-indexing**: When you need to rebuild the entire document index
- **Recovery**: After ingestion service failures or data loss

### Prerequisites

1. Database must be accessible (MongoDB)
2. Ingestion service must be running at the configured URL (default: `http://localhost:8001`)
3. Application configuration files must be properly set up

### How to Run

#### Option 1: From IntelliJ IDEA
1. Right-click on `BulkDocumentIngestionUtil.java`
2. Select "Run 'BulkDocumentIngestionUtil.main()'"
3. Monitor the console output for progress

#### Option 2: From Maven Command Line
```bash
mvn spring-boot:run -Dspring-boot.run.main-class=com.pma.util.BulkDocumentIngestionUtil
```

#### Option 3: From Compiled JAR
```bash
java -cp target/pma-app.jar com.pma.util.BulkDocumentIngestionUtil
```

### What It Does

1. Connects to the database
2. Fetches all documents
3. Iterates through each document and calls the ingestion service
4. Adds a small delay (200ms) between requests to avoid overwhelming the service
5. Prints a summary report

### Output Example

```
========================================
Bulk Document Ingestion Utility
========================================
Fetching all documents from database...
Found 15 document(s) to ingest
----------------------------------------
[1/15] Ingesting document: ID=DOC001, Title='Project Requirements'
[2/15] Ingesting document: ID=DOC002, Title='Technical Specification'
...
Waiting for async operations to complete...
========================================
Ingestion Summary:
  Total documents: 15
  Successfully queued: 15
  Errors: 0
========================================
Note: Check ingestion service logs to verify all documents were processed.
Bulk ingestion utility completed.
```

### Notes

- The script uses the **same async ingestion client** as the main application
- Errors are logged but don't stop the process - all documents are attempted
- Check the ingestion service logs to confirm all documents were actually indexed
- The script exits automatically after completion

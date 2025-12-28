// Document View Handler - Loads document data via REST API
document.addEventListener('DOMContentLoaded', function () {
    // Get document ID from URL path
    const pathParts = window.location.pathname.split('/');
    const documentId = pathParts[pathParts.length - 1];

    if (documentId && documentId !== 'view') {
        loadDocumentDetails(documentId);
    }
});

async function loadDocumentDetails(documentId) {
    try {
        const response = await fetch(`/api/documents/${documentId}`);

        if (response.ok) {
            const document = await response.json();
            displayDocument(document);
        } else if (response.status === 404) {
            showError('Document not found');
        } else {
            showError('Failed to load document');
        }
    } catch (error) {
        console.error('Error loading document:', error);
        showError('Network error while loading document');
    }
}

function displayDocument(doc) {
    // Update title
    const titleElement = document.getElementById('documentTitle');
    if (titleElement) {
        titleElement.textContent = doc.title;
    }

    // Update content - Render markdown as HTML
    const contentElement = document.getElementById('documentContent');
    if (contentElement && doc.content) {
        // Parse markdown and render as HTML
        contentElement.innerHTML = marked.parse(doc.content);
    }


    // Update author
    const authorElement = document.getElementById('documentAuthor');
    if (authorElement && doc.author) {
        authorElement.textContent = `Author: ${doc.author}`;
    }

    // Update created date
    const createdElement = document.getElementById('documentCreated');
    if (createdElement && doc.createdDate) {
        const date = new Date(doc.createdDate);
        createdElement.textContent = `Created: ${date.toLocaleDateString()} ${date.toLocaleTimeString()}`;
    }

    // Update updated date
    const updatedElement = document.getElementById('documentUpdated');
    if (updatedElement && doc.updatedDate) {
        const date = new Date(doc.updatedDate);
        updatedElement.textContent = `Last Updated: ${date.toLocaleDateString()} ${date.toLocaleTimeString()}`;
    }

    // Update reviewer info
    const reviewerElement = document.getElementById('documentReviewer');
    if (reviewerElement) {
        if (doc.reviewer && doc.reviewedDate) {
            const date = new Date(doc.reviewedDate);
            reviewerElement.innerHTML = `
                <span class="badge bg-success">Reviewed</span> 
                by ${doc.reviewer} on ${date.toLocaleDateString()}
            `;
        } else {
            reviewerElement.innerHTML = '<span class="badge bg-warning">Not Reviewed</span>';
        }
    }

    // Display tags if available
    displayTags(doc.tags);

    // Update action buttons with document ID
    const updateButton = document.getElementById('updateButton');
    if (updateButton) {
        updateButton.href = `/documents/update/${doc.id}`;
    }

    const deleteButton = document.getElementById('deleteButton');
    if (deleteButton) {
        deleteButton.onclick = () => handleDelete(doc.id);
    }

    const reviewButton = document.getElementById('reviewButton');
    if (reviewButton) {
        reviewButton.onclick = () => handleReview(doc.id);
    }

    // Hide loading indicator
    const loadingElement = document.getElementById('loading');
    if (loadingElement) {
        loadingElement.style.display = 'none';
    }

    // Show content
    const contentContainer = document.getElementById('documentContainer');
    if (contentContainer) {
        contentContainer.style.display = 'block';
    }
}

/**
 * Display document tags in the metadata section
 * @param {Array} tags - Array of tag strings
 */
function displayTags(tags) {
    const tagsSection = document.getElementById('documentTagsSection');
    const tagsDisplay = document.getElementById('documentTagsDisplay');

    if (!tagsSection || !tagsDisplay) {
        return;
    }

    // Clear existing tags
    tagsDisplay.innerHTML = '';

    // Show section only if there are tags
    if (tags && Array.isArray(tags) && tags.length > 0) {
        tagsSection.style.display = 'block';

        // Create badge for each tag
        tags.forEach(tag => {
            const tagBadge = document.createElement('span');
            tagBadge.className = 'tag-badge-view';
            tagBadge.textContent = tag;
            tagsDisplay.appendChild(tagBadge);
        });
    } else {
        tagsSection.style.display = 'none';
    }
}

async function handleDelete(documentId) {
    if (!confirm('Are you sure you want to delete this document?')) {
        return;
    }

    try {
        const response = await fetch(`/api/documents/${documentId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('Document deleted successfully');
            window.location.href = '/documents';
        } else {
            alert('Failed to delete document');
        }
    } catch (error) {
        console.error('Error deleting document:', error);
        alert('Network error while deleting document');
    }
}

async function handleReview(documentId) {
    try {
        const response = await fetch(`/api/documents/${documentId}/review`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const updatedDocument = await response.json();
            displayDocument(updatedDocument); // Refresh the display
            alert('Document marked as reviewed');
        } else if (response.status === 403) {
            alert('You do not have permission to review documents');
        } else if (response.status === 401) {
            alert('Please log in to review documents');
        } else {
            alert('Failed to mark document as reviewed');
        }
    } catch (error) {
        console.error('Error reviewing document:', error);
        alert('Network error while reviewing document');
    }
}

function showError(message) {
    const errorElement = document.getElementById('errorMessage');
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.display = 'block';
    }

    const loadingElement = document.getElementById('loading');
    if (loadingElement) {
        loadingElement.style.display = 'none';
    }
}

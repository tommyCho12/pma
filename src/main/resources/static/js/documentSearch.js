const searchInput = document.getElementById('searchInput');
const documentsBody = document.getElementById('documentsBody');
const resultsCount = document.getElementById('resultsCount');
const noResults = document.getElementById('noResults');
const loadingSpinner = document.querySelector('.loading-spinner');
const documentsTable = document.getElementById('documentsTable');

let searchTimeout;
let allDocuments = [];

// Load all documents on page load
async function loadDocuments() {
    try {
        showLoading(true);
        const response = await fetch('/api/documents/all');
        allDocuments = await response.json();
        displayDocuments(allDocuments);
    } catch (error) {
        console.error('Error loading documents:', error);
        documentsBody.innerHTML = '<tr><td colspan="4" class="text-center text-danger">Error loading documents</td></tr>';
    } finally {
        showLoading(false);
    }
}

// Real-time search as user types
searchInput.addEventListener('input', function () {
    clearTimeout(searchTimeout);
    const keyword = this.value.trim();

    showLoading(true);

    // Debounce: wait 300ms after user stops typing
    searchTimeout = setTimeout(() => {
        if (keyword === '') {
            displayDocuments(allDocuments);
        } else {
            searchDocuments(keyword);
        }
        showLoading(false);
    }, 300);
});

// Search documents (client-side filtering for instant results)
function searchDocuments(keyword) {
    const lowerKeyword = keyword.toLowerCase();

    const filtered = allDocuments.filter(doc => {
        const title = (doc.title || '').toLowerCase();
        const content = (doc.content || '').toLowerCase();
        const id = (doc.id || '').toLowerCase();
        return title.includes(lowerKeyword) || content.includes(lowerKeyword) || id.includes(lowerKeyword);
    });

    displayDocuments(filtered, keyword);
}

// Display documents in table
function displayDocuments(documents, keyword = '') {
    documentsBody.innerHTML = '';

    if (documents.length === 0) {
        documentsTable.style.display = 'none';
        noResults.style.display = 'block';
        resultsCount.textContent = '';
        return;
    }

    documentsTable.style.display = 'table';
    noResults.style.display = 'none';

    // Update results count
    if (keyword) {
        resultsCount.textContent = `Found ${documents.length} document${documents.length !== 1 ? 's' : ''} matching "${keyword}"`;
    } else {
        resultsCount.textContent = `Showing all ${documents.length} document${documents.length !== 1 ? 's' : ''}`;
    }

    documents.forEach(doc => {
        const row = createDocumentRow(doc, keyword);
        documentsBody.appendChild(row);
    });
}

// Create table row for document
function createDocumentRow(doc, keyword = '') {
    const row = document.createElement('tr');
    row.className = 'document-row';
    row.style.cursor = 'pointer';

    // Add reviewed row styling if document is reviewed
    if (doc.reviewedDate) {
        row.classList.add('reviewed-row');
    }

    row.onclick = () => {
        window.location.href = `/documents/view/${doc.id}`;
    };

    // Document ID with review checkmark if reviewed
    const idCell = document.createElement('td');
    const reviewCheckmark = doc.reviewedDate ? '<span class="review-checkmark">✅</span>' : '';
    idCell.innerHTML = `${reviewCheckmark}<code>${doc.id}</code>`;
    idCell.style.fontSize = '0.9rem';

    // Title with highlighting
    const titleCell = document.createElement('td');
    titleCell.innerHTML = highlightText(doc.title || 'Untitled', keyword);

    // Content preview with highlighting
    const contentCell = document.createElement('td');
    const contentPreview = (doc.content || '').substring(0, 150);
    const truncated = (doc.content || '').length > 150 ? '...' : '';
    contentCell.innerHTML = highlightText(contentPreview + truncated, keyword);
    contentCell.style.color = '#6c757d';

    // Tags
    const tagsCell = document.createElement('td');
    // Create badge for each tag
    if (doc.tags) {
        doc.tags.forEach(tag => {
            const tagBadge = document.createElement('span');
            tagBadge.className = 'tag-badge-view';
            tagBadge.textContent = tag;
            tagsCell.appendChild(tagBadge);
        });
    }
    // Actions
    const actionsCell = document.createElement('td');

    let actionsHTML = `
        <a href="/documents/view/${doc.id}" 
           class="btn btn-sm btn-outline-info me-1"
           onclick="event.stopPropagation()">
            👁️ View
        </a>
    `;

    // Only add Edit and Delete buttons if user is authenticated
    if (window.isUserAuthenticated) {
        actionsHTML += `
        <a href="/documents/update/${doc.id}" 
           class="btn btn-sm btn-outline-primary me-1"
           onclick="event.stopPropagation()">
            ✏️ Edit
        </a>
        <button onclick="deleteDocument('${doc.id}', '${escapeHtml(doc.title)}'); event.stopPropagation();" 
                class="btn btn-sm btn-outline-danger">
            🗑️ Delete
        </button>
        `;
    }

    actionsCell.innerHTML = actionsHTML;

    row.appendChild(idCell);
    row.appendChild(titleCell);
    row.appendChild(contentCell);
    row.appendChild(tagsCell);
    row.appendChild(actionsCell);

    return row;
}

// Highlight matching text
function highlightText(text, keyword) {
    if (!keyword || !text) return escapeHtml(text);

    const escaped = escapeHtml(text);
    const regex = new RegExp(`(${escapeRegex(keyword)})`, 'gi');
    return escaped.replace(regex, '<span class="highlight">$1</span>');
}

// Escape HTML to prevent XSS
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Escape regex special characters
function escapeRegex(str) {
    return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

// Show/hide loading spinner
function showLoading(show) {
    loadingSpinner.style.display = show ? 'block' : 'none';
}

// Delete document
async function deleteDocument(id, title) {
    if (!confirm(`Are you sure you want to delete "${title}"?`)) {
        return;
    }

    try {
        const response = await fetch(`/api/documents/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            // Remove from local array
            allDocuments = allDocuments.filter(doc => doc.id !== id);
            // Re-apply current search
            const keyword = searchInput.value.trim();
            if (keyword) {
                searchDocuments(keyword);
            } else {
                displayDocuments(allDocuments);
            }
        } else {
            alert('Error deleting document');
        }
    } catch (error) {
        console.error('Error deleting document:', error);
        alert('Error deleting document');
    }
}

// Load documents when page loads
loadDocuments();
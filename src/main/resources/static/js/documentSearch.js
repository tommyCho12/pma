const searchInput = document.getElementById('searchInput');
const documentsBody = document.getElementById('documentsBody');
const resultsCount = document.getElementById('resultsCount');
const noResults = document.getElementById('noResults');
const loadingSpinner = document.querySelector('.loading-spinner');
const documentsTable = document.getElementById('documentsTable');

// Filter elements
const tagSearchInput = document.getElementById('tagSearchInput');
const tagOptionsDropdown = document.getElementById('tagOptionsDropdown');
const selectedTagsDisplay = document.getElementById('selectedTagsDisplay');
const reviewedCheckbox = document.getElementById('reviewedCheckbox');

let searchTimeout;
let allDocuments = [];
let allTags = [];
let selectedTags = [];

// Load all documents on page load
async function loadDocuments() {
    try {
        showLoading(true);
        const response = await fetch('/api/documents/all');
        allDocuments = await response.json();
        extractAllTags();
        displayDocuments(allDocuments);
    } catch (error) {
        console.error('Error loading documents:', error);
        documentsBody.innerHTML = '<tr><td colspan="5" class="text-center text-danger">Error loading documents</td></tr>';
    } finally {
        showLoading(false);
    }
}

// Extract all unique tags from documents
function extractAllTags() {
    const tagSet = new Set();
    allDocuments.forEach(doc => {
        if (doc.tags && Array.isArray(doc.tags)) {
            doc.tags.forEach(tag => tagSet.add(tag));
        }
    });
    allTags = Array.from(tagSet).sort();
}

// Real-time search as user types
searchInput.addEventListener('input', function () {
    clearTimeout(searchTimeout);
    showLoading(true);

    // Debounce: wait 300ms after user stops typing
    searchTimeout = setTimeout(() => {
        applyFilters();
        showLoading(false);
    }, 300);
});

// Tag search input - filter dropdown options
tagSearchInput.addEventListener('input', function () {
    const searchTerm = this.value.toLowerCase().trim();
    displayTagOptions(searchTerm);
});

// Show dropdown when tag search is focused
tagSearchInput.addEventListener('focus', function () {
    displayTagOptions(this.value.toLowerCase().trim());
});

// Hide dropdown when clicking outside
document.addEventListener('click', function (e) {
    if (!e.target.closest('.tag-filter-wrapper')) {
        tagOptionsDropdown.style.display = 'none';
    }
});

// Reviewed checkbox change
reviewedCheckbox.addEventListener('change', function () {
    applyFilters();
});

// Display tag options in dropdown
function displayTagOptions(searchTerm) {
    tagOptionsDropdown.innerHTML = '';

    const filteredTags = allTags.filter(tag =>
        tag.toLowerCase().includes(searchTerm)
    );

    if (filteredTags.length === 0) {
        tagOptionsDropdown.innerHTML = '<div class="tag-option-empty">No tags found</div>';
        tagOptionsDropdown.style.display = 'block';
        return;
    }

    filteredTags.forEach(tag => {
        const option = document.createElement('div');
        option.className = 'tag-option';
        if (selectedTags.includes(tag)) {
            option.classList.add('selected');
        }
        option.textContent = tag;
        option.addEventListener('click', () => toggleTagSelection(tag));
        tagOptionsDropdown.appendChild(option);
    });

    tagOptionsDropdown.style.display = 'block';
}

// Toggle tag selection
function toggleTagSelection(tag) {
    const index = selectedTags.indexOf(tag);
    if (index > -1) {
        selectedTags.splice(index, 1);
    } else {
        selectedTags.push(tag);
    }
    updateSelectedTagsDisplay();
    displayTagOptions(tagSearchInput.value.toLowerCase().trim());
    applyFilters();
}

// Update selected tags display
function updateSelectedTagsDisplay() {
    selectedTagsDisplay.innerHTML = '';

    if (selectedTags.length === 0) {
        selectedTagsDisplay.style.display = 'none';
        return;
    }

    selectedTagsDisplay.style.display = 'flex';

    selectedTags.forEach(tag => {
        const badge = document.createElement('span');
        badge.className = 'selected-tag-badge';
        badge.innerHTML = `
            <span>${tag}</span>
            <span class="selected-tag-remove" data-tag="${tag}">×</span>
        `;

        badge.querySelector('.selected-tag-remove').addEventListener('click', (e) => {
            e.stopPropagation();
            toggleTagSelection(tag);
        });

        selectedTagsDisplay.appendChild(badge);
    });
}

// Apply all filters (search + tags + reviewed)
function applyFilters() {
    const keyword = searchInput.value.trim().toLowerCase();
    const onlyReviewed = reviewedCheckbox.checked;

    const filtered = allDocuments.filter(doc => {
        // Search filter
        if (keyword) {
            const title = (doc.title || '').toLowerCase();
            const content = (doc.content || '').toLowerCase();
            const id = (doc.id || '').toLowerCase();
            const matchesSearch = title.includes(keyword) || content.includes(keyword) || id.includes(keyword);
            if (!matchesSearch) return false;
        }

        // Tag filter
        if (selectedTags.length > 0) {
            const docTags = doc.tags || [];
            const hasSelectedTag = selectedTags.some(tag => docTags.includes(tag));
            if (!hasSelectedTag) return false;
        }

        // Reviewed filter
        if (onlyReviewed && !doc.reviewedDate) {
            return false;
        }

        return true;
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

    // Update results count with filter information
    const filterDescriptions = [];
    if (keyword) filterDescriptions.push(`search: "${keyword}"`);
    if (selectedTags.length > 0) filterDescriptions.push(`tags: ${selectedTags.join(', ')}`);
    if (reviewedCheckbox.checked) filterDescriptions.push('reviewed only');

    if (filterDescriptions.length > 0) {
        resultsCount.textContent = `Found ${documents.length} document${documents.length !== 1 ? 's' : ''} (${filterDescriptions.join('; ')})`;
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
            // Re-extract tags and re-apply filters
            extractAllTags();
            applyFilters();
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
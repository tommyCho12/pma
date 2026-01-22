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
        documentsBody.innerHTML = '<div class="col-span-full text-center text-red-500 py-8">Error loading documents</div>';
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

// Display documents as cards
function displayDocuments(documents, keyword = '') {
    documentsBody.innerHTML = '';

    if (documents.length === 0) {
        noResults.classList.remove('hidden');
        resultsCount.textContent = '';
        return;
    }

    noResults.classList.add('hidden');

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
        const card = createDocumentCard(doc, keyword);
        documentsBody.appendChild(card);
    });
}

// Create card for document
function createDocumentCard(doc, keyword = '') {
    const card = document.createElement('div');
    card.className = 'group bg-white rounded-2xl shadow-lg shadow-slate-200/50 hover:shadow-xl hover:shadow-slate-300/50 border border-slate-100 overflow-hidden transition-all duration-300 hover:-translate-y-1 cursor-pointer';

    card.onclick = (e) => {
        if (!e.target.closest('a') && !e.target.closest('button')) {
            window.location.href = `/documents/view/${doc.id}`;
        }
    };

    // Content preview
    const contentPreview = (doc.content || '').substring(0, 100);
    const truncated = (doc.content || '').length > 100 ? '...' : '';

    // Review status badge
    const reviewBadge = doc.reviewedDate
        ? '<span class="inline-flex items-center gap-1 px-2 py-1 bg-emerald-100 text-emerald-700 text-xs font-medium rounded-full"><i class="bi bi-check-circle-fill"></i> Reviewed</span>'
        : '<span class="inline-flex items-center gap-1 px-2 py-1 bg-amber-100 text-amber-700 text-xs font-medium rounded-full"><i class="bi bi-clock"></i> Pending</span>';

    // Tags HTML
    let tagsHTML = '';
    if (doc.tags && doc.tags.length > 0) {
        tagsHTML = doc.tags.map(tag =>
            `<span class="inline-block px-2 py-0.5 bg-gradient-to-r from-primary-500 to-accent-purple text-white text-xs font-medium rounded-full">${escapeHtml(tag)}</span>`
        ).join('');
    }

    // Actions HTML
    let actionsHTML = `
        <a href="/documents/view/${doc.id}" 
           class="flex-1 inline-flex items-center justify-center gap-2 px-4 py-2 bg-slate-100 hover:bg-primary-500 text-slate-700 hover:text-white font-medium rounded-lg transition-all duration-200"
           onclick="event.stopPropagation()">
            <i class="bi bi-eye"></i>
            View
        </a>
    `;

    if (window.isUserAuthenticated) {
        actionsHTML += `
        <a href="/documents/update/${doc.id}" 
           class="flex-1 inline-flex items-center justify-center gap-2 px-4 py-2 bg-slate-100 hover:bg-amber-500 text-slate-700 hover:text-white font-medium rounded-lg transition-all duration-200"
           onclick="event.stopPropagation()">
            <i class="bi bi-pencil"></i>
            Edit
        </a>
        <button onclick="deleteDocument('${doc.id}', '${escapeHtml(doc.title)}'); event.stopPropagation();" 
                class="p-2 bg-slate-100 hover:bg-red-500 text-slate-500 hover:text-white rounded-lg transition-all duration-200">
            <i class="bi bi-trash"></i>
        </button>
        `;
    }

    card.innerHTML = `
        <div class="p-6">
            <!-- Header with ID and Review Status -->
            <div class="flex items-center justify-between mb-3">
                <span class="px-2.5 py-1 bg-slate-100 text-slate-600 text-xs font-mono rounded-lg">${escapeHtml(doc.id)}</span>
                ${reviewBadge}
            </div>
            
            <!-- Title -->
            <h3 class="text-lg font-semibold text-slate-800 mb-2 line-clamp-2 group-hover:text-primary-600 transition-colors">
                ${highlightText(doc.title || 'Untitled', keyword)}
            </h3>
            
            <!-- Content Preview -->
            <p class="text-slate-500 text-sm line-clamp-3 mb-4">
                ${highlightText(contentPreview + truncated, keyword)}
            </p>
            
            <!-- Tags -->
            <div class="flex flex-wrap gap-1.5 mb-4 min-h-[24px]">
                ${tagsHTML}
            </div>
            
            <!-- Actions -->
            <div class="flex items-center gap-2 pt-4 border-t border-slate-100">
                ${actionsHTML}
            </div>
        </div>
    `;

    return card;
}

// Highlight matching text
function highlightText(text, keyword) {
    if (!keyword || !text) return escapeHtml(text);

    const escaped = escapeHtml(text);
    const regex = new RegExp(`(${escapeRegex(keyword)})`, 'gi');
    return escaped.replace(regex, '<mark class="bg-amber-200 text-amber-900 px-0.5 rounded">$1</mark>');
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
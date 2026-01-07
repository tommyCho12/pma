// Document Form Handler
// Handles markdown editor initialization, form validation, and REST API operations

let easyMDE;
let documentTags = []; // Array to store document tags

/**
 * Initialize the EasyMDE markdown editor
 */
function initializeEditor() {
    const editorElement = document.getElementById('contentEditor');

    if (!editorElement) {
        console.log('Editor element not found - skipping EasyMDE initialization');
        return;
    }

    easyMDE = new EasyMDE({
        element: editorElement,
        spellChecker: false,
        placeholder: "Write your document content here using Markdown...\n\nExamples:\n# Heading 1\n## Heading 2\n\n**Bold text**\n*Italic text*\n\n- List item 1\n- List item 2",
        status: ["lines", "words", "cursor"],
        toolbar: [
            "bold",
            "italic",
            "heading",
            "|",
            "quote",
            "unordered-list",
            "ordered-list",
            "|",
            "link",
            "image",
            "|",
            "preview",
            "side-by-side",
            "fullscreen",
            "|",
            "guide"
        ],
        renderingConfig: {
            singleLineBreaks: false,
            codeSyntaxHighlighting: true,
        },
        minHeight: "300px",
        maxHeight: "600px",
    });
}

/**
 * Validate form content before submission
 * @param {Event} e - Submit event
 * @returns {boolean} Whether validation passed
 */
function validateContent(e) {
    // Only validate if EasyMDE is initialized
    if (easyMDE) {
        const content = easyMDE.value().trim();

        if (!content) {
            e.preventDefault();
            alert('Please enter document content');
            return false;
        }

        // Ensure the content is synced to the textarea
        document.getElementById('contentEditor').value = content;
    }
    return true;
}

/**
 * Handle form submission via REST API
 */
function setupFormSubmission() {
    const documentForm = document.getElementById('documentForm');

    if (!documentForm) {
        console.error('Document form not found');
        return;
    }

    documentForm.addEventListener('submit', async function (e) {
        e.preventDefault(); // Prevent traditional form submission

        // Validate content first
        if (!validateContent(e)) {
            return;
        }

        const formData = new FormData(documentForm);
        const documentId = formData.get('id');
        const isUpdate = documentId && documentId.trim() !== '';

        const documentData = {
            title: formData.get('title'),
            content: formData.get('content'),
            tags: documentTags // Include tags in submission
        };

        // Add ID for updates
        if (isUpdate) {
            documentData.id = documentId;
        }

        try {
            let response;

            if (isUpdate) {
                // PUT request for update
                response = await fetch(`/api/documents/${documentId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(documentData)
                });
            } else {
                //POST request for create
                response = await fetch('/api/documents', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(documentData)
                });
            }

            if (response.ok) {
                const result = await response.json();
                console.log('Document saved:', result);

                // Show success message
                showMessage('success', `Document ${isUpdate ? 'updated' : 'created'} successfully!`);

                // Redirect to documents page after a brief delay
                setTimeout(() => {
                    window.location.href = '/documents';
                }, 1000);
            } else {
                const error = await response.text();
                console.error('Error saving document:', error);
                showMessage('error', `Failed to ${isUpdate ? 'update' : 'create'} document. Please try again.`);
            }
        } catch (error) {
            console.error('Network error:', error);
            showMessage('error', 'Network error. Please check your connection and try again.');
        }
    });
}

/**
 * Initialize tags from existing document data
 * Called when editing an existing document
 */
function initializeTags() {
    // Check if tags were provided by Thymeleaf inline script
    if (window.initialDocumentTags && Array.isArray(window.initialDocumentTags) && window.initialDocumentTags.length > 0) {
        documentTags = window.initialDocumentTags;
        renderTags();
    } else {
        documentTags = [];
    }
}

/**
 * Add a new tag to the document
 * @param {string} tagText - The tag text to add
 */
function addTag(tagText) {
    // Trim and normalize the tag
    const tag = tagText.trim().toLowerCase();

    // Validate tag
    if (!tag) {
        return;
    }

    // Check for duplicates
    if (documentTags.includes(tag)) {
        showTagMessage('This tag already exists', 'warning');
        return;
    }

    // Add tag to array
    documentTags.push(tag);

    // Update UI
    renderTags();

    // Clear input
    document.getElementById('tagInput').value = '';
}

/**
 * Remove a tag from the document
 * @param {number} index - Index of the tag to remove
 */
function removeTag(index) {
    documentTags.splice(index, 1);
    renderTags();
}

/**
 * Render all tags in the UI
 */
function renderTags() {
    const container = document.getElementById('tagsContainer');

    // Clear container
    container.innerHTML = '';

    // Render each tag
    documentTags.forEach((tag, index) => {
        const tagBadge = document.createElement('div');
        tagBadge.className = 'tag-badge';

        const tagText = document.createElement('span');
        tagText.className = 'tag-text';
        tagText.textContent = tag;

        const removeBtn = document.createElement('button');
        removeBtn.className = 'tag-remove';
        removeBtn.innerHTML = '×';
        removeBtn.type = 'button';
        removeBtn.setAttribute('aria-label', `Remove ${tag} tag`);
        removeBtn.addEventListener('click', () => removeTag(index));

        tagBadge.appendChild(tagText);
        tagBadge.appendChild(removeBtn);
        container.appendChild(tagBadge);
    });

    // Update hidden input for Thymeleaf form binding
    document.getElementById('tagsData').value = JSON.stringify(documentTags);
}

/**
 * Setup tag input event listeners
 */
function setupTagInput() {
    const tagInput = document.getElementById('tagInput');
    const addTagBtn = document.getElementById('addTagBtn');

    if (!tagInput || !addTagBtn) {
        return;
    }

    // Add tag on button click
    addTagBtn.addEventListener('click', () => {
        addTag(tagInput.value);
    });

    // Add tag on Enter key
    tagInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            addTag(tagInput.value);
        }
    });
}

/**
 * Show temporary message for tag operations
 * @param {string} message - Message to display
 * @param {string} type - Message type (warning, info)
 */
function showTagMessage(message, type) {
    const tagInput = document.getElementById('tagInput');

    // Create message element
    const messageDiv = document.createElement('div');
    messageDiv.className = `tag-message tag-message-${type}`;
    messageDiv.textContent = message;
    messageDiv.style.cssText = `
        font-size: 12px;
        color: ${type === 'warning' ? '#856404' : '#004085'};
        background: ${type === 'warning' ? '#fff3cd' : '#cce5ff'};
        padding: 5px 10px;
        border-radius: 4px;
        margin-top: 5px;
        animation: tagFadeIn 0.3s ease;
    `;

    // Insert after tag input wrapper
    const wrapper = tagInput.parentElement;
    wrapper.parentElement.insertBefore(messageDiv, wrapper.nextSibling);

    // Auto-remove after 2 seconds
    setTimeout(() => {
        messageDiv.remove();
    }, 2000);
}

/**
 * Display success or error messages to the user
 * @param {string} type - 'success' or 'error'
 * @param {string} message - Message to display
 */
function showMessage(type, message) {
    // Remove any existing messages
    const existingMessage = document.querySelector('.form-message');
    if (existingMessage) {
        existingMessage.remove();
    }

    // Create message element
    const messageDiv = document.createElement('div');
    messageDiv.className = `form-message alert alert-${type === 'success' ? 'success' : 'danger'}`;
    messageDiv.textContent = message;
    messageDiv.style.marginTop = '1rem';

    // Insert after form
    const form = document.getElementById('documentForm');
    form.parentNode.insertBefore(messageDiv, form.nextSibling);

    // Auto-remove after 5 seconds for error messages
    if (type === 'error') {
        setTimeout(() => {
            messageDiv.remove();
        }, 5000);
    }
}

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', function () {
    initializeEditor();
    setupFormSubmission();
    initializeTags();      // Load existing tags if editing
    setupTagInput();       // Setup tag input handlers
});

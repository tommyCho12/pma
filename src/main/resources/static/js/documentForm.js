// Document Form Handler
// Handles markdown editor initialization, form validation, and REST API operations

let easyMDE;

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
            content: formData.get('content')
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
});

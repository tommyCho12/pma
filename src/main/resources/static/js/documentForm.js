// Document Form Handler - Uses REST API for create/update operations
document.addEventListener('DOMContentLoaded', function () {
    const documentForm = document.getElementById('documentForm');

    if (documentForm) {
        documentForm.addEventListener('submit', async function (e) {
            e.preventDefault(); // Prevent traditional form submission

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
});

// Helper function to show messages
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

import { test, expect } from '@playwright/test';
import { loginAsUser, loginAsReviewer, logout, createDocumentViaUi, navigateToDocuments, searchDocument, createDocument } from './common_steps';

// const documentsToRemove: string[] = [];

// test.afterEach(async ({ request }) => {
//     for (const id of documentsToRemove) {
//         await request.delete(`/api/documents/${encodeURIComponent(id)}`);
//     }
//     documentsToRemove.length = 0;
// });

test('basic workflow: user creates document', async ({ page }) => {
    // Step 1: Log in as a User and create a doc
    await loginAsUser(page);
    await expect(page.getByText('user@example.com')).toBeVisible();
    const documentTitle = `Test Document ${Date.now()}`;
    const documentContent = 'This is a test document for the basic workflow test.';
    await createDocumentViaUi(page, documentTitle, documentContent);

    // Step 2: Navigate to document home page and check if the document is available
    await page.waitForSelector('#documentsTable tbody tr');
    await searchDocument(page, documentTitle);
    const documentRow = page.locator('#documentsTable tbody tr', { hasText: documentTitle });
    await expect(documentRow).toBeVisible();
    // Verify the document content preview is visible
    await expect(documentRow.locator('td').nth(2)).toContainText(documentContent.substring(0, 50));
});

test('basic workflow: reviewer reviews document', async ({ page }) => {
    const documentTitle = `Test Document ${Date.now()}`;
    const documentContent = 'This is a test document for the basic workflow test.';

    await loginAsUser(page);
    await createDocument(page, documentTitle, documentContent);
    await logout(page);
    await loginAsReviewer(page);
    await expect(page.getByText('reviewer@example.com')).toBeVisible();
    await navigateToDocuments(page);

    // Open the document
    await searchDocument(page, documentTitle);
    const reviewerDocumentRow = page.locator('#documentsTable tbody tr', { hasText: documentTitle });
    await expect(reviewerDocumentRow).toBeVisible();
    await reviewerDocumentRow.locator('a', { hasText: 'View' }).click();
    await page.waitForSelector('#documentTitle');
    await expect(page.locator('#documentTitle')).toContainText(documentTitle);

    // Click the "Mark as Reviewed" button (only visible to reviewers)
    const reviewButton = page.locator('#reviewButton');
    await expect(reviewButton).toBeVisible();
    await reviewButton.click();
    await page.reload();
    await page.waitForSelector('#documentReviewer');

    // Check if the document is marked as reviewed
    const reviewerElement = page.locator('#documentReviewer');
    await expect(reviewerElement).toContainText('Reviewed');
    await expect(reviewerElement).toContainText('reviewer@example.com');

    // Navigate back to documents home and check for the review checkmark (✅) in the document ID cell
    await navigateToDocuments(page);
    const reviewedDocumentRow = page.locator('#documentsTable tbody tr', { hasText: documentTitle });
    await expect(reviewedDocumentRow).toBeVisible();
    const idCell = reviewedDocumentRow.locator('td').first();
    await expect(idCell).toContainText('✅');
});
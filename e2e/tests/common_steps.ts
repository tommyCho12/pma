import { Page } from 'playwright';

export async function login(page: Page, username: string = 'reviewer@example.com', password: string = 'reviewer') {
    await page.goto('/');
    await page.getByText('Log in').click();
    await page.getByPlaceholder('Username').fill(username);
    await page.getByPlaceholder('Password').fill(password);
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.waitForSelector('a[href="/logout"]');
}

export async function loginAsReviewer(page: Page) {
    await login(page, 'reviewer@example.com', 'reviewer');
}

export async function loginAsUser(page: Page) {
    await login(page, 'user@example.com', 'user');
}

export async function loginAsAdmin(page: Page) {
    await login(page, 'multi@example.com', 'multi');
}

export async function logout(page: Page) {
    await page.getByText('Logout').click();
    await page.waitForSelector('text=Please sign in');
}

export async function navigateToDocuments(page: Page) {
    await page.goto('/documents');
    await page.waitForLoadState('networkidle');
}

export async function createDocument(page: Page, title: string, content: string, tags?: string[]) {
    // API request to create document
    const response = await page.request.post('/api/documents', {
        headers: {
            'Content-Type': 'application/json'
        },
        data: {
            title: title,
            content: content,
            tags: tags || []
        }
    });

    if (response.status() !== 201) {
        throw new Error(`Failed to create document via API. Status: ${response.status()} ${response.statusText()}`);
    }
}

export async function createDocumentViaUi(page: Page, title: string, content: string, tags?: string[]) {
    await navigateToDocuments(page);

    // Look for "New Document" or "Add Document" button
    const newDocButton = page.locator('button:has-text("New"), a:has-text("New"), button:has-text("Add"), a:has-text("Add")').first();
    await newDocButton.click();

    // Fill in the form
    await page.getByLabel('Title', { exact: false }).fill(title);
    await page.locator('.CodeMirror').click();
    await page.keyboard.type(content);

    if (tags) {
        const tagInput = page.locator('#tagInput');
        for (const tag of tags) {
            await tagInput.fill(tag);
            await page.keyboard.press('Enter');
        }
    }

    // Submit the form
    await page.locator('button[type="submit"], button:has-text("Save"), button:has-text("Create")').first().click();

    // Wait for navigation or success message
    await page.waitForLoadState('networkidle');
}

export async function searchDocument(page: Page, titleOrContent: string) {
    await navigateToDocuments(page);
    await page.locator('#searchInput').fill(titleOrContent);
}
import { test, expect } from '@playwright/test';
import { login } from './common_steps';

test.beforeAll(async ({ browser }) => {
    const page = await browser.newPage();
    await login(page);
    // await page.close();
});

test('user can logout', async ({ page }) => {
    await page.getByText('Logout').click();
    await expect(page.getByText('Please sign in')).toBeVisible();
    await expect(page.getByText('You have been signed out')).toBeVisible();
});
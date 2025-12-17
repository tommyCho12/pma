import { test, expect } from '@playwright/test';

test('has title', async ({ page }) => {
    await page.goto('https://playwright.dev/');

    // Expect a title "to contain" a substring.
    await expect(page).toHaveTitle(/Playwright/);
});

test('get started link', async ({ page }) => {
    await page.goto('https://playwright.dev/');

    // Click the get started link.
    await page.getByRole('link', { name: 'Get started' }).click();

    // Expects page to have a heading with the name of Installation.
    await expect(page.getByRole('heading', { name: 'Installation' })).toBeVisible();
});

test('custom locator', async ({ page }) => {
    await page.goto('https://playwright.dev/docs/ci-intro');

    //get element by css locator
    const getStartedLink = await page.locator("a[class^='navbar'][href*='/docs/api']").click();

    // Expects page to have a heading with the name of Installation.
    await expect(page.getByRole('heading', { name: 'Playwright Library' })).toBeVisible();
});

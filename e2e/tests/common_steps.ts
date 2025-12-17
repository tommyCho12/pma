import { Page } from 'playwright';

export async function login(page: Page) {
    await page.goto('http://localhost:8080');
    await page.getByText('Log in').click();
    await page.getByPlaceholder('Username').fill('reviewer@example.com');
    await page.getByPlaceholder('Password').fill('reviewer');
    await page.getByRole('button', { name: 'Sign in' }).click();
    await page.waitForSelector('a[href="/logout"]');
}
import { browser } from "vibium";

const vibe = await browser.launch();
await vibe.go("https://example.com");
const el = await vibe.find("a");
await el.click();
await vibe.quit();
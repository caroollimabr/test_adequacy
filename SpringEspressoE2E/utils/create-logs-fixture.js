const { test: base } = require('@playwright/test');
const fs = require('fs');
const path = require('path');

const createLogsFixture = base.extend({
  context: async ({ browser }, use, testInfo) => {
    const featureName = path.basename(testInfo.file).replace(/\.(spec|test)\.js$/, '');
    const featureDir = path.join(process.cwd(), 'logs', featureName);

    if (!fs.existsSync(featureDir)) {
      fs.mkdirSync(featureDir, { recursive: true });
    }

    const safeName = testInfo.title.replace(/[^a-zA-Z0-9]/g, '-');
    const harPath = path.join(featureDir, `${safeName}.har`);

    const context = await browser.newContext({
      recordHar: { 
        path: harPath, 
        content: 'embed', 
        urlFilter: /^http:\/\/localhost:8080\/.*/ 
      }
    });

    await context.route(url => !url.href.startsWith('http://localhost:8080'), route => {
      route.abort();
    });

    await use(context);
    await context.close();
  }
});

module.exports = { createLogsFixture };
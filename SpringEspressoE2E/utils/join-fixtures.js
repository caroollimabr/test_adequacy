const { mergeTests, expect } = require('@playwright/test');
const { dbFixture } = require('./db-fixture');
const { createLogsFixture } = require('./create-logs-fixture');

// colocando todas as fixtures em conjunto
const test = mergeTests(createLogsFixture, dbFixture);

module.exports = { test, expect };
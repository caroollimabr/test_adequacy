const { test, expect } = require('../utils/join-fixtures');
const { LoginPage } = require('../pages/LoginPage');

let loginPage;

test.beforeEach(async ({ page }) => {
    loginPage = new LoginPage(page);

    await loginPage.goto();
    
  });

  test('must log in with success when admin', async ({ page }) => {
    const email = 'admin@admin.com';
    await loginPage.login(email, 'admin');

    await loginPage.expectWelcomeMessage(email);
    await loginPage.expectAdminMenuVisible(true);
    await loginPage.expectCommonMenuVisible();
  });

  test('must log in with success when user', async ({ page }) => {
    const email = 'user@user.com';
    await loginPage.login(email, 'user');

    await loginPage.expectWelcomeMessage(email);
    await loginPage.expectAdminMenuVisible(false);
    await loginPage.expectCommonMenuVisible();
  });

  test('must log out with success', async ({ page }) => {
    await loginPage.login('admin@admin.com', 'admin');
    await loginPage.logout();
    
    await expect(loginPage.logoutSuccessMessage).toBeVisible();
  });

  test('must not log in when the profile does not exist', async ({ page }) => {
    await loginPage.login('sicrano@sicrano.com', 'sicrano');

    await expect(page).toHaveTitle("Login");
    await expect(loginPage.errorMessage).toBeVisible();
  });
const { expect } = require('@playwright/test');

class LoginPage {
  constructor(page) {
    this.page = page;

    //input locators
    this.usernameInput = page.locator('#username');
    this.passwordInput = page.locator('#password');
    this.loginButton = page.locator('#login-button');

    //message locators
    this.errorMessage = page.getByText('Email ou senha inválidos.');
    this.logoutSuccessMessage = page.getByText('Você foi desconectado com sucesso');

    //menu and navigation
    this.header = page.locator('#site-header');
    this.logoutButton = this.header.getByRole('button', { name: 'Sair' });

    //features
    this.manageUsersButton = page.getByRole('link', { name: 'Gerenciar Usuários' });
    this.viewProjectsButton = page.getByRole('link', { name: 'Ver Projetos' });
    this.viewStrategiesButton = page.getByRole('link', { name: 'Ver Estratégias' });
    this.viewBugsButton = page.getByRole('link', { name: 'Ver Bugs' });
  }

  //Methods
  async goto() {
    await this.page.goto('http://localhost:8080');
  }

  async login(username, password) {
    await this.usernameInput.fill(username);
    await this.passwordInput.fill(password);
    await this.loginButton.click();
  }

  async logout() {
    await this.logoutButton.click();
  }

  //Assertions
  async expectWelcomeMessage(email) {
    const welcomeHeading = this.page.getByRole('heading', { name: `Bem-vindo(a), ${email} !` });
    await expect(welcomeHeading).toBeVisible();
  }

  async expectAdminMenuVisible(isAdmin = true) {
    if (isAdmin) {
      await expect(this.manageUsersButton).toBeVisible();
    } else {
      await expect(this.manageUsersButton).not.toBeVisible();
    }
  }

  async expectCommonMenuVisible() {
    await expect(this.viewProjectsButton).toBeVisible();
    await expect(this.viewStrategiesButton).toBeVisible();
    await expect(this.viewBugsButton).toBeVisible();
  }
}


module.exports = { LoginPage };
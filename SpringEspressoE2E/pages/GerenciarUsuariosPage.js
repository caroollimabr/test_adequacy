const { expect } = require('@playwright/test');

class GerenciarUsuariosPage {
  constructor(page) {
    this.page = page;

    //Headings
    this.headingManageUsers = page.getByRole('heading', { name: 'Gerenciar Usuários' });
    this.headingUserInfo = page.getByRole('heading', { name: 'Informações do Usuário' });
    this.headingNewUser = page.getByRole('heading', { name: 'Novo Usuário' });
    
    //Buttons
    this.newUserButton = page.getByRole('link', { name: 'Novo Usuário' });
    this.goBackToListButton = page.locator('div').filter({ hasText: 'Voltar à Lista' }).nth(1);
    this.goBackHomeButton = page.getByRole('link', { name: 'Voltar ao Home' });
    this.createUserButton = page.getByRole('button', { name: 'Criar Usuário' });
    this.updateUserButton = page.getByRole('button', { name: 'Atualizar Usuário' });
    this.cancelButton = page.getByRole('link', { name: 'Cancelar' });
    this.editThisUserButton = page.getByRole('link', { name: 'Editar Este Usuário' });
    this.removeUserButton = page.getByRole('link', { name: 'Remover Usuário' });
    
    //Forms
    this.inputName = page.getByRole('textbox', { name: 'Nome Completo *' });
    this.inputEmail = page.getByRole('textbox', { name: 'Email *' });
    this.inputPassword = page.getByRole('textbox', { name: 'Senha' });
    this.selectProfile = page.getByLabel('Perfil de Acesso *');
  }

  //Methods
  getUserRow(email) {
    return this.page.locator('tr', { hasText: email });
  }

  async createUser() {
    await this.newUserButton.click();
  }

  async fillOutForm(name, email, password, profile) {
    if (name) await this.inputName.fill(name);
    if (email) await this.inputEmail.fill(email);
    if (password) await this.inputPassword.fill(password);
    if (profile) await this.selectProfile.selectOption(profile);
  }

  async saveNewUser() {
    await this.createUserButton.click();
  }

  async updateUser() {
    await this.updateUserButton.click();
  }

  async seeListDetails(email) {
    await this.getUserRow(email).getByRole('link', { name: 'Ver detalhes' }).click();
  }

  async editUser(email) {
    await this.getUserRow(email).getByRole('link', { name: 'Editar' }).click();
  }

  async removeUser(email) {
    this.page.once('dialog', async dialog => {
      await dialog.accept();
    });
    await this.getUserRow(email).getByRole('link', { name: 'Remover' }).click();
  }

  async removeUserDetailsPage() {
    this.page.once('dialog', async dialog => {
      await dialog.accept();
    });
    await this.removeUserButton.click();
  }

  //Assertions
  async expectMessage(message) {
    await expect(this.page.locator('body')).toContainText(message);
  }

  async expectUserOnTheList(name, email, visible = true) {
    const nameCell = this.page.getByRole('cell', { name: name, exact: true });
    const emailCell = this.page.getByRole('cell', { name: email, exact: true });
    
    if (visible) {
      await expect(nameCell).toBeVisible();
      await expect(emailCell).toBeVisible();
    } else {
      await expect(nameCell || emailCell).not.toBeVisible();
    }
  }

  async expectDetailsPermissions(permissions) {
    for (const permission of permissions) {
      await expect(this.page.getByText(permission)).toBeVisible();
    }
  }
}

module.exports = { GerenciarUsuariosPage };
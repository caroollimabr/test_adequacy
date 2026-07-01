const { test, expect } = require('../utils/join-fixtures');
const { LoginPage } = require('../pages/LoginPage');
const { GerenciarUsuariosPage } = require('../pages/GerenciarUsuariosPage');

let loginPage;
let manageUsersPage;

test.beforeEach(async ({ page }) => {
  loginPage = new LoginPage(page);
  manageUsersPage = new GerenciarUsuariosPage(page);

  await loginPage.goto();
  await loginPage.login('admin@admin.com', 'admin');
  await loginPage.manageUsersButton.click();
});

test('must see user details (Admin) and go Home', async ({ page }) => {
  //seeing user details
  await manageUsersPage.seeListDetails('admin@admin.com');

  await expect(manageUsersPage.headingUserInfo).toBeVisible();
  await manageUsersPage.expectDetailsPermissions([
    'Nome: Admin',
    'Email: admin@admin.com',
    'Administrador',
    'Permissões: Gerenciar usuários do sistema',
    'Criar, editar e excluir projetos',
    'Gerenciar estratégias',
    'Acesso completo ao sistema'
  ]);

  //going Home
  await manageUsersPage.goBackToListButton.click();
  await manageUsersPage.goBackHomeButton.click();

  await loginPage.expectWelcomeMessage('admin@admin.com');
  await loginPage.expectAdminMenuVisible(true);
});

test('must see user details (User) and edit them', async ({ page }) => {
  //seeing user details
  await manageUsersPage.seeListDetails('useruser@user.com');
  await manageUsersPage.expectDetailsPermissions([
    'Nome: User Usuario',
    'Email: useruser@user.com',
    'Permissões: Participar de projetos como membro',
    'Visualizar e usar estratégias',
    'Registrar sessões de teste',
    'Reportar bugs'
  ]);

  //going to editing page from Details Page
  await manageUsersPage.editThisUserButton.click();

  await expect(manageUsersPage.inputName).toHaveValue('User Usuario');
  await expect(manageUsersPage.inputEmail).toHaveValue('useruser@user.com');
  await expect(manageUsersPage.inputPassword).toBeEmpty();
  await expect(manageUsersPage.selectProfile).toHaveValue('ROLE_USER');

  //editing user
  await manageUsersPage.fillOutForm('User User', null, null, null);
  await manageUsersPage.updateUser();
  
  await manageUsersPage.expectMessage('Usuário atualizado com sucesso!');
  await manageUsersPage.expectUserOnTheList('User User', 'user@user.com', true);
  await manageUsersPage.expectUserOnTheList('User Usuario', 'user@user.com', false);

  //going back to how it was
  await manageUsersPage.seeListDetails('useruser@user.com');
  await manageUsersPage.editThisUserButton.click();
  await manageUsersPage.fillOutForm('User Usuario', null, null, null);
  await manageUsersPage.updateUser();

  await manageUsersPage.expectUserOnTheList('User Usuario', 'user@user.com', true);
});

test('must not leave the new user page when mandatory fields are not filled', async ({ page }) => {
  await manageUsersPage.createUser();
  await manageUsersPage.saveNewUser();

  await expect(manageUsersPage.headingNewUser).toBeVisible();
});

test('must leave the new user page when pressing the button Cancelar without adding the new user', async ({ page }) => {
  await manageUsersPage.createUser();
  await manageUsersPage.fillOutForm('Sicrano de Tal', 'sicrano@sicrano.com', 'sicrano', 'ROLE_ADMIN');
  await manageUsersPage.cancelButton.click();

  await expect(manageUsersPage.headingManageUsers).toBeVisible();

  await manageUsersPage.expectUserOnTheList('Beltrano de Tal', 'beltrano@beltrano.com', false);
});

test('must create new user with user permissions and remove it in the page Ver detalhes', async ({ page }) => {
  //creating new user
  await manageUsersPage.createUser();
  await manageUsersPage.fillOutForm('Fulano de Tal', 'fulano@fulano.com', 'fulano', 'ROLE_USER');
  await manageUsersPage.saveNewUser();

  await manageUsersPage.expectMessage('Usuário criado com sucesso!');
  await expect(manageUsersPage.headingManageUsers).toBeVisible();
  await manageUsersPage.expectUserOnTheList('Fulano de Tal', 'fulano@fulano.com', true);
  await manageUsersPage.seeListDetails('fulano@fulano.com');
  
  //removing user
  await manageUsersPage.removeUserDetailsPage();

  await manageUsersPage.expectMessage('Usuário removido com sucesso!');
  await expect(manageUsersPage.headingManageUsers).toBeVisible();
  await manageUsersPage.expectUserOnTheList('Fulano de Tal', 'fulano@fulano.com', false);
});

test('must create new user with admin permissions, see it on the list, succeed when logging in, and removing it', async ({ page }) => {
  //creating new user
  await manageUsersPage.createUser();
  await manageUsersPage.fillOutForm('Administrador Dois', 'admin2@admin.com', 'admin2', 'ROLE_ADMIN');
  await manageUsersPage.saveNewUser();

  await manageUsersPage.expectMessage('Usuário criado com sucesso!');
  await manageUsersPage.expectUserOnTheList('Administrador Dois', 'admin2@admin.com', true);
  
  // Logout
  await loginPage.logout();
  await expect(loginPage.logoutSuccessMessage).toBeVisible();

  // Login (new user)
  await loginPage.login('admin2@admin.com', 'admin2');
  await expect(page).toHaveTitle("Home - Spring Espresso Game Testing");
  await loginPage.expectWelcomeMessage('admin2@admin.com');

  // Logout and login
  await loginPage.logout();
  await loginPage.login('admin@admin.com', 'admin');
  
  // Removing new user
  await loginPage.manageUsersButton.click();
  await manageUsersPage.removeUser('admin2@admin.com');
  
  await manageUsersPage.expectMessage('Usuário removido com sucesso!');
  await manageUsersPage.expectUserOnTheList('Administrador Dois', 'admin2@admin.com', false);
});

test('must create new user with admin permissions, edit it, and succeed when logging in and removing it', async ({ page }) => {
  //creating new user
  await manageUsersPage.createUser();
  await manageUsersPage.fillOutForm('Administrador Tres', 'admin3@admin.com', 'admin3', 'ROLE_ADMIN');
  await manageUsersPage.saveNewUser();
  
  await manageUsersPage.expectMessage('Usuário criado com sucesso!');
  
  // Editing user
  await manageUsersPage.editUser('admin3@admin.com');
  await manageUsersPage.fillOutForm('Administrador Edit', 'adminedit@admin.co', 'adminEdit', null);
  await manageUsersPage.updateUser();
  
  await manageUsersPage.expectMessage('Usuário atualizado com sucesso!');
  await manageUsersPage.expectUserOnTheList('Administrador Edit', 'adminedit@admin.co', true);

  // Login with edited user
  await loginPage.logout();
  await loginPage.login('adminedit@admin.co', 'adminEdit');
  await loginPage.expectWelcomeMessage('adminedit@admin.co');
  
  // Logout and login
  await loginPage.logout();
  await loginPage.login('admin@admin.com', 'admin');
  
  //removing new user
  await loginPage.manageUsersButton.click();
  await manageUsersPage.removeUser('adminedit@admin.co');
  
  await manageUsersPage.expectMessage('Usuário removido com sucesso!');
  await manageUsersPage.expectUserOnTheList('Administrador Edit', 'adminedit@admin.co', false);
});
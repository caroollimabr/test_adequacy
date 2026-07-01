const { test, expect } = require('../utils/join-fixtures');
const { LoginPage } = require('../pages/LoginPage');
const { GerenciarProjetosPage } = require('../pages/GerenciarProjetosPage');

let loginPage;
let manageProjectsPage;

test.describe.configure({ mode: 'default' }); // run one test after another

  test.beforeEach(async ({ page }) => {
    loginPage = new LoginPage(page);
    manageProjectsPage = new GerenciarProjetosPage(page);

    await loginPage.goto();
    await loginPage.login('admin@admin.com', 'admin');
    await manageProjectsPage.goToProjects();
  });

  test('must cancel new project', async ({ page }) => {
    await manageProjectsPage.startNewProject();
    await manageProjectsPage.fillProjectDetails('ProjetoTesteAdmin1', 'DescricaoProjetoTesteAdmin1', ['Admin', 'Maria Silva']);
    await manageProjectsPage.linkCancel.click();

    await expect(page.locator('body')).not.toContainText('ProjetoTesteAdmin1 DescricaoProjetoTesteAdmin1');
  });

  test('must create a new project, see its details, and edit it', async ({ page }) => {
    // new project
    await manageProjectsPage.startNewProject();
    await manageProjectsPage.fillProjectDetails('ProjetoTesteAdmin2', 'DescricaoProjetoTesteAdmin2', ['Admin', 'Maria Silva']);
    await manageProjectsPage.btnSave.click();

    await expect(page.getByText('Projeto cadastrado com sucesso!')).toBeVisible();
    
    const rowProjetoTesteAdmin2 = page.getByRole('row').filter({ hasText: 'ProjetoTesteAdmin2' });
    await expect(page.locator('body')).toContainText('ProjetoTesteAdmin2 DescricaoProjetoTesteAdmin2 Nenhuma sessão finalizada');
    //await expect(rowProjetoTesteAdmin2.locator('body')).toContainText('Admin'); 
    //await expect(rowProjetoTesteAdmin2.locator('body')).toContainText('Maria Silva'); 
    
    await manageProjectsPage.clickProjectAction('ProjetoTesteAdmin2', 'Ver Detalhes');
    await expect(page.locator('body')).toContainText('ProjetoTesteAdmin2 DescricaoProjetoTesteAdmin2 Estratégias do Projeto');
    await manageProjectsPage.linkBackToList.click();

    //editing/updating project
    await manageProjectsPage.clickProjectAction('ProjetoTesteAdmin2', 'Editar');

    await manageProjectsPage.fillProjectDetails('ProjetoTesteAdmin22', 'DescricaoProjetoTesteAdmin22');
    await manageProjectsPage.toggleMember('Maria Silva', false); // uncheck
    await manageProjectsPage.btnUpdate.click();

    await expect(page.getByText('Projeto atualizado com sucesso!')).toBeVisible();
    await expect(page.locator('body')).toContainText('ProjetoTesteAdmin22 DescricaoProjetoTesteAdmin22 Nenhuma sessão finalizada Admin');
    
    //seeing project details and add new session
    await manageProjectsPage.clickProjectAction('ProjetoTesteAdmin22', 'Ver Detalhes');
    await manageProjectsPage.accessStrategy('User Interface');
    await manageProjectsPage.startNewSession();
    
    await manageProjectsPage.fillSessionDetails('DescricaoSessaoTesteAdmin3', '88', '1');
    await manageProjectsPage.saveSession();
    await expect(page.getByText('Sessão criada com sucesso!')).toBeVisible();

    //possible to delete when session has "created" status
    await expect(page.locator('tbody')).toContainText('Criado Admin 88 DescricaoSessaoTesteAdmin3 Detalhes Excluir');
    await manageProjectsPage.accessSessionAction('DescricaoSessaoTesteAdmin3', 'Detalhes');
    
    await manageProjectsPage.changeExecutionStatus(true); // Iniciar
    await expect(page.getByText('Status da sessão atualizado')).toBeVisible();
    await expect(page.getByText('Status Atual: Em Execução')).toBeVisible();
    await manageProjectsPage.linkBackToSessions.click();

    //not possible to delete when it's in execution or ended
    await expect(page.locator('tbody')).toContainText('Em Execução Admin 88 DescricaoSessaoTesteAdmin3 Detalhes');
    await expect(page.locator('tbody')).not.toContainText('Excluir');
    
    await manageProjectsPage.accessSessionAction('DescricaoSessaoTesteAdmin3', 'Detalhes');
    await manageProjectsPage.changeExecutionStatus(false); // Finalizar
    await expect(page.getByText('Status Atual: Finalizado')).toBeVisible();
    await manageProjectsPage.linkBackToSessions.click();
    
    await expect(page.locator('tbody')).toContainText('Finalizado Admin 88 DescricaoSessaoTesteAdmin3 Detalhes');
    await expect(page.locator('tbody')).not.toContainText('Excluir');

    //new session
    await manageProjectsPage.startNewSession();
    await manageProjectsPage.fillSessionDetails('DescricaoSessaoTesteAdmin4', '0245', '1');
    await manageProjectsPage.saveSession();
    
    await expect(page.getByText('Sessão criada com sucesso!')).toBeVisible();
    await expect(page.locator('tbody')).toContainText('Criado Admin 245 DescricaoSessaoTesteAdmin4 Detalhes Excluir');
    
    //deleting session
    page.once('dialog', dialog => {
      console.log(`Dialog message: ${dialog.message()}`);
      dialog.accept().catch(() => {});
    });
    await manageProjectsPage.btnDelete.click();
    
    await expect(page.getByText('Sessão removida com sucesso!')).toBeVisible();
    await expect(page.locator('tbody')).not.toContainText('Criado Admin 245 DescricaoSessaoTesteAdmin4');
  });

  test('must create a new project and cancel its edition', async ({ page }) => {
    //new project
    await manageProjectsPage.startNewProject();
    await manageProjectsPage.fillProjectDetails('ProjetoTesteAdmin3', 'DescricaoProjetoTesteAdmin3', ['Admin', 'Maria Silva']);
    await manageProjectsPage.btnSave.click();
    
    await expect(page.getByText('Projeto cadastrado com sucesso!')).toBeVisible();
    await expect(page.locator('body')).toContainText('ProjetoTesteAdmin3 DescricaoProjetoTesteAdmin3 Nenhuma sessão finalizada');
    
    await manageProjectsPage.clickProjectAction('ProjetoTesteAdmin3', 'Ver Detalhes');
    await expect(page.locator('body')).toContainText('ProjetoTesteAdmin3 DescricaoProjetoTesteAdmin3 Estratégias do Projeto');
    await manageProjectsPage.linkBackToList.click();
    
    //canceling project edition
    await manageProjectsPage.clickProjectAction('ProjetoTesteAdmin3', 'Editar');

    await manageProjectsPage.fillProjectDetails('ProjetoTesteAdmin33', 'DescricaoProjetoTesteAdmin33');
    await manageProjectsPage.toggleMember('Maria Silva', false); // uncheck

    await manageProjectsPage.linkCancel.click();
    
    await expect(page.locator('body')).not.toContainText('ProjetoTesteAdmin33 DescricaoProjetoTesteAdmin33');
    await expect(page.locator('body')).toContainText('ProjetoTesteAdmin3 DescricaoProjetoTesteAdmin3 Nenhuma sessão finalizada'); 
  });

  test('must create a new project and remove it', async ({ page }) => {
    //new project
    await manageProjectsPage.startNewProject();
    await manageProjectsPage.fillProjectDetails('ProjetoTesteAdmin4', 'DescricaoProjetoTesteAdmin4', ['Maria Silva']);
    await manageProjectsPage.btnSave.click();
    
    await expect(page.getByText('Projeto cadastrado com sucesso!')).toBeVisible();
    await expect(page.locator('body')).toContainText('ProjetoTesteAdmin4 DescricaoProjetoTesteAdmin4 Nenhuma sessão finalizada Maria Silva');
    
    //removing project
    page.once('dialog', dialog => {
      console.log(`Dialog message: ${dialog.message()}`);
      dialog.accept().catch(() => {});
    });
    
    await manageProjectsPage.clickProjectAction('ProjetoTesteAdmin4', 'Remover');

    await expect(page.getByText('Projeto removido com sucesso!')).toBeVisible();
    await expect(page.locator('body')).not.toContainText('ProjetoTesteAdmin4 DescricaoProjetoTesteAdmin4 Estratégias do Projeto');
  });

  test('must create a new project and cancel adding a session', async ({ page }) => {
    //new project
    await manageProjectsPage.startNewProject();
    await manageProjectsPage.fillProjectDetails('ProjetoTesteAdmin5', 'DescricaoProjetoTesteAdmin5', ['Admin', 'Maria Silva']);
    await manageProjectsPage.btnSave.click();
    
    await expect(page.getByText('Projeto cadastrado com sucesso!')).toBeVisible();
    await expect(page.locator('body')).toContainText('ProjetoTesteAdmin5 DescricaoProjetoTesteAdmin5 Nenhuma sessão finalizada');
    
    await manageProjectsPage.clickProjectAction('ProjetoTesteAdmin5', 'Ver Detalhes');
    
    //new session + canceling
    await manageProjectsPage.accessStrategy('Neighboring');
    await manageProjectsPage.startNewSession();
    await manageProjectsPage.fillSessionDetails('DescricaoSessaoTesteAdmin5', '0055', '1');
    
    await manageProjectsPage.linkCancel.click();
    
    await expect(page.locator('tbody')).not.toContainText('Criado Maria Silva 55 DescricaoSessaoTesteAdmin5 Detalhes Excluir');
  });


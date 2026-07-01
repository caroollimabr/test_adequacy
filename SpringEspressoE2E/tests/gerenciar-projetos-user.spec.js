const { test, expect } = require('../utils/join-fixtures');
const { LoginPage } = require('../pages/LoginPage');
const { GerenciarProjetosPage } = require('../pages/GerenciarProjetosPage');
const mysql = require('mysql2/promise');

let loginPage;
let manageProjectsPage;

  test.beforeEach(async ({ page }) => {
    loginPage = new LoginPage(page);
    manageProjectsPage = new GerenciarProjetosPage(page);

    await loginPage.goto();
    await loginPage.login('user@user.com', 'user');
    await manageProjectsPage.goToProjects();
  });

  test('must add a new complete session, add a bug, see its details and the bug report, end the session, and go back Home passing through all involved screens', async ({ page }) => {
    //creating new session
    await manageProjectsPage.enterProjectFromUserTable('Projeto Alpha Teste Projeto');

    await expect(page.getByRole('link', { name: 'Noob Journey' })).toBeVisible();
    
    await manageProjectsPage.accessStrategy('Noob Journey');
    await manageProjectsPage.startNewSession();

    await manageProjectsPage.fillSessionDetails('SessãoTesteUser1\nDado que blabla\nQuando blabla\nEntão blabla', '60');
    await manageProjectsPage.saveSession();

    await expect(page.getByText('Sessão criada com sucesso!')).toBeVisible();
    await expect(page.getByRole('row', { name: 'Criado Maria Silva 60 SessãoTesteUser1' })).toBeVisible();

    //starting its execution
    await manageProjectsPage.accessSessionAction('Criado Maria Silva 60 SessãoTesteUser1');
    await manageProjectsPage.changeExecutionStatus(true); // Iniciar Execução

    await expect(page.getByText('Status da sessão atualizado')).toBeVisible();
    await expect(page.getByText('Status Atual: Em Execução')).toBeVisible();

    //adding bug
    await manageProjectsPage.startNewBug();
    await manageProjectsPage.fillBugDetails('BugTesteUser1', 'CRITICA', 'bug.png');
    await manageProjectsPage.saveBug();

    await expect(page.getByRole('cell', { name: 'BugTesteUser1' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Crítica' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Aberto' })).toBeVisible();

    await page.getByText('📷 Imagem').click();

    await expect(page.getByRole('img', { name: 'Screenshot do bug' })).toBeVisible();
    await expect(page.getByText('SessãoTesteUser1 Dado que blabla Quando blabla Então blabla')).toBeVisible();

    await page.getByRole('link', { name: 'SessãoTesteUser1 Dado que blabla\nQuando blabla\nEntão blabla' }).click();

    await expect(page.getByRole('heading', { name: 'Detalhes da Sessão' })).toBeVisible();

    //seeing bug through Bug report
    await page.getByRole('cell', { name: 'BugTesteUser1' }).click();

    await expect(page.getByRole('heading', { name: 'Detalhes do Bug' })).toBeVisible();

    await page.getByRole('link', { name: 'Voltar para Lista de Bugs' }).click();

    await expect(page.getByRole('heading', { name: 'Relatório de Bugs' })).toBeVisible();

    await page.getByRole('link', { name: 'Voltar' }).click();

    //going back Home and accessing Session
    await expect(page.getByRole('heading', { name: 'Bem-vindo(a), user@user.com !' })).toBeVisible();

    await manageProjectsPage.goToProjects();
    await manageProjectsPage.enterProjectFromUserTable('Projeto Alpha Teste Projeto');
    await manageProjectsPage.accessStrategy('Noob Journey');
    await manageProjectsPage.accessSessionAction('Em Execução Maria Silva 60 SessãoTesteUser1');

    //ending session
    await manageProjectsPage.changeExecutionStatus(false); // Finalizar Sessão

    await expect(page.getByText('Status da sessão atualizado')).toBeVisible();
    await expect(page.getByText('Status Atual: Finalizado')).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Sessão concluída' })).toBeVisible();

    //going back Home screen by screen
    await manageProjectsPage.linkBackToSessions.click();

    await expect(page.getByRole('heading', { name: 'Sessões de Teste' })).toBeVisible();

    await page.getByRole('link', { name: 'Voltar ao Projeto' }).click();

    await expect(page.getByRole('heading', { name: 'Estratégias do Projeto' })).toBeVisible();

    await manageProjectsPage.linkBackToList.click();

    await expect(page.getByRole('heading', { name: 'Meus Projetos' })).toBeVisible();

    await manageProjectsPage.linkMainMenu.click();

    await expect(page.getByRole('heading', { name: 'Bem-vindo(a), user@user.com !' })).toBeVisible();
  });

  test('must add a new complete session, delete it and go back straight Home', async ({ page }) => {
    //creating new session
    await manageProjectsPage.enterProjectFromUserTable('Projeto Beta Teste Projeto');

    await expect(page.getByRole('link', { name: 'Completionist' })).toBeVisible();
    
    await manageProjectsPage.accessStrategy('Completionist');
    await manageProjectsPage.startNewSession();

    await manageProjectsPage.fillSessionDetails('SessãoTesteUser2\nDado blabla\nQuando blabla\nEntão blabla', '16');
    await manageProjectsPage.saveSession();

    await expect(page.getByText('Sessão criada com sucesso!')).toBeVisible();
    await expect(page.getByRole('row', { name: 'Criado Maria Silva 16 SessãoTesteUser2' })).toBeVisible();
    
    //removing session
    page.once('dialog', dialog => {
      console.log(`Dialog message: ${dialog.message()}`);
      dialog.accept().catch(() => {});
    });
    await manageProjectsPage.btnDelete.click();

    await expect(page.getByText('Sessão removida com sucesso!')).toBeVisible();

    //going straight Home
    await manageProjectsPage.linkMainMenu.click();

    await expect(page.getByRole('heading', { name: 'Bem-vindo(a), user@user.com !' })).toBeVisible();
  });

  test('must cancel creating a session', async ({ page }) => {
    //creating new session
    await manageProjectsPage.enterProjectFromUserTable('Projeto Beta Teste Projeto');

    await expect(page.getByRole('link', { name: 'Neighboring' })).toBeVisible();
    
    await manageProjectsPage.accessStrategy('Neighboring');
    await manageProjectsPage.startNewSession();
    
    await manageProjectsPage.fillSessionDetails('SessãoTesteUser3\nDado blabla\nQuando blabla\nEntão blabla', '123');
    
    //canceling session creation
    await manageProjectsPage.linkCancel.click();

    await expect(page.getByText('Sessão criada com sucesso!')).not.toBeVisible();
    await expect(page.getByRole('row', { name: 'Criado Maria Silva 123 SessãoTesteUser3' })).not.toBeVisible();
  });

  test('must create a session and cancel adding a new bug, going back straight Home', async ({ page }) => {
    //creating new session
    await manageProjectsPage.enterProjectFromUserTable('Projeto Alpha Teste Projeto');

    await expect(page.getByRole('link', { name: 'User Interface' })).toBeVisible();
    
    await manageProjectsPage.accessStrategy('User Interface');
    await manageProjectsPage.startNewSession();

    await manageProjectsPage.fillSessionDetails('SessãoTesteUser4\nDado blabla\nQuando blabla\nEntão blabla', '10000');
    await manageProjectsPage.saveSession();

    await expect(page.getByText('Sessão criada com sucesso!')).toBeVisible();
    await expect(page.getByRole('row', { name: 'Criado Maria Silva 10000 SessãoTesteUser4' })).toBeVisible();

    //starting its execution
    await manageProjectsPage.accessSessionAction('Criado Maria Silva 10000 SessãoTesteUser4');
    await manageProjectsPage.changeExecutionStatus(true); // Iniciar Execução

    await expect(page.getByText('Status da sessão atualizado')).toBeVisible();
    await expect(page.getByText('Status Atual: Em Execução')).toBeVisible();

    //adding bug
    await manageProjectsPage.startNewBug();
    await manageProjectsPage.fillBugDetails('BugTesteUser2', 'BAIXA');
    await manageProjectsPage.linkCancel.click(); // Cancelando

    await expect(page.getByRole('cell', { name: 'BugTesteUser2' })).not.toBeVisible();
    await expect(page.getByRole('cell', { name: 'Baixa' })).not.toBeVisible();
    await expect(page.getByRole('cell', { name: 'Aberto' })).not.toBeVisible();
    await expect(page.getByRole('heading', { name: 'Detalhes da Sessão' })).toBeVisible();

    //going straight Home from Session details
    await manageProjectsPage.linkMainMenu.click();

    await expect(page.getByRole('heading', { name: 'Bem-vindo(a), user@user.com !' })).toBeVisible();
  });

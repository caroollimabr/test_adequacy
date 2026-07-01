const { test, expect } = require('../utils/join-fixtures');
const { LoginPage } = require('../pages/LoginPage');
const { GerenciarProjetosPage } = require('../pages/GerenciarProjetosPage');
const { BugsEncontradosPage } = require('../pages/BugsEncontradosPage');

let loginPage;
let manageProjectsPage;
let bugsReportPage;

test.describe.configure({ mode: 'default' });

  test.beforeEach(async ({ page }) => {
    loginPage = new LoginPage(page);
    manageProjectsPage = new GerenciarProjetosPage(page);
    bugsReportPage = new BugsEncontradosPage(page);

    await loginPage.goto();
  });

  test('must add 2 new bugs as a user and see them in the bug report', async ({ page }) => {
    await loginPage.login('user@user.com', 'user');
    await manageProjectsPage.goToProjects();
    
    // creating new session
    await manageProjectsPage.enterProjectFromUserTable('Projeto Alpha Teste Projeto');
    
    await manageProjectsPage.accessStrategy('Noob Journey');
    await manageProjectsPage.startNewSession();
    await manageProjectsPage.fillSessionDetails('SessãoTesteBug1\nDado que blabla\nQuando blabla\nEntão blabla', '60');
    await manageProjectsPage.saveSession();

    await expect(page.getByText('Sessão criada com sucesso!')).toBeVisible();
    await expect(page.getByRole('row', { name: 'Criado Maria Silva 60 SessãoTesteBug1' })).toBeVisible();

    // starting its execution
    await manageProjectsPage.accessSessionAction('Criado Maria Silva 60 SessãoTesteBug1');
    await manageProjectsPage.changeExecutionStatus(true); // Iniciar Execução

    await expect(page.getByText('Status da sessão atualizado')).toBeVisible();
    await expect(page.getByText('Status Atual: Em Execução')).toBeVisible();
    
    // adding first bug
    await manageProjectsPage.startNewBug();
    await manageProjectsPage.fillBugDetails('BugBugTeste1', 'Média', 'bug.png');
    await manageProjectsPage.saveBug();

    await expect(page.getByRole('cell', { name: 'BugBugTeste1' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Média' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Aberto' })).toBeVisible();

    // interacting with bug features
    await bugsReportPage.viewAttachedImage();

    await expect(bugsReportPage.imgScreenshot).toBeVisible();
    await expect(bugsReportPage.bodyContent).toContainText('SessãoTesteBug1 Dado que blabla Quando blabla Então blabla');

    await bugsReportPage.goBackToSession('SessãoTesteBug1 Dado que blabla\nQuando blabla\nEntão blabla');

    await expect(bugsReportPage.getHeadingByText('Detalhes da Sessão')).toBeVisible();

    // adding second bug
    await manageProjectsPage.startNewBug();
    await manageProjectsPage.fillBugDetails('BugBugTeste2', 'Baixa');
    await manageProjectsPage.saveBug();

    await expect(page.getByRole('cell', { name: 'BugBugTeste2' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Baixa' })).toBeVisible();
    await expect(bugsReportPage.getHeadingByText('Detalhes da Sessão')).toBeVisible();

    // seeing first bug through Bug report
    await bugsReportPage.viewBugDetailsFromSession('BugBugTeste2');

    await expect(bugsReportPage.getHeadingByText('Detalhes do Bug')).toBeVisible();
    await expect(bugsReportPage.bodyContent).toContainText('Status: Aberto');
    await expect(bugsReportPage.btnResolve).not.toBeVisible();

    await bugsReportPage.linkBackToBugList.click();

    await expect(bugsReportPage.getHeadingByText('Relatório de Bugs')).toBeVisible();
    await expect(bugsReportPage.bodyContent).toContainText('Projeto Alpha BugBugTeste1 Média ❌');
    await expect(bugsReportPage.bodyContent).toContainText('Projeto Alpha BugBugTeste2 Baixa ❌');

    await bugsReportPage.linkBack.click();
    await bugsReportPage.goToBugsReport();

    await expect(bugsReportPage.getHeadingByText('Relatório de Bugs')).toBeVisible();
    await expect(bugsReportPage.bodyContent).toContainText('Projeto Alpha BugBugTeste1 Média ❌');
    await expect(bugsReportPage.bodyContent).toContainText('Projeto Alpha BugBugTeste2 Baixa ❌');

    await bugsReportPage.linkBack.click();

    await expect(page.getByRole('heading', { name: 'Bem-vindo(a), user@user.com !' })).toBeVisible();
  });

  test('must add 2 new bugs as an admin, resolve them, and reopen one of them', async ({ page }) => {
    await loginPage.login('admin@admin.com', 'admin');
    await manageProjectsPage.goToProjects();
    
    // creating new session
    await page.getByRole('link', { name: 'Ver Detalhes' }).first().click(); 
    await expect(page.getByRole('link', { name: 'Overtime' })).toBeVisible();
    
    await manageProjectsPage.accessStrategy('Overtime');
    await manageProjectsPage.startNewSession();
    await manageProjectsPage.fillSessionDetails('SessãoTesteBug3', '60', '2');
    await manageProjectsPage.saveSession();

    await expect(page.getByText('Sessão criada com sucesso!')).toBeVisible();
    await expect(page.locator('tbody')).toContainText('Criado Maria Silva 60 SessãoTesteBug3 Detalhes Excluir');

    // starting its execution
    await manageProjectsPage.accessSessionAction('Criado Maria Silva 60 SessãoTesteBug3');
    await manageProjectsPage.changeExecutionStatus(true);

    await expect(page.getByText('Status da sessão atualizado')).toBeVisible();
    await expect(page.getByText('Status Atual: Em Execução')).toBeVisible();
    
    // adding first bug
    await manageProjectsPage.startNewBug();
    await manageProjectsPage.fillBugDetails('BugBugTeste3', 'Média', 'bug.png');
    await manageProjectsPage.saveBug();

    await expect(bugsReportPage.bodyContent).toContainText('BugBugTeste3 Média Aberto');

    await bugsReportPage.viewAttachedImage();

    await expect(bugsReportPage.imgScreenshot).toBeVisible();
    
    await bugsReportPage.goBackToSession('SessãoTesteBug3');

    await expect(bugsReportPage.getHeadingByText('Detalhes da Sessão')).toBeVisible();
    
    // adding second bug
    await manageProjectsPage.startNewBug();
    await manageProjectsPage.fillBugDetails('BugBugTeste4', 'Baixa');
    await manageProjectsPage.saveBug();

    await expect(bugsReportPage.bodyContent).toContainText('BugBugTeste4 Baixa Aberto');
    await expect(bugsReportPage.getHeadingByText('Detalhes da Sessão')).toBeVisible();
    
    // seeing bug through Bug report
    await bugsReportPage.viewBugDetailsFromSession('BugTeste3');

    await expect(bugsReportPage.getHeadingByText('Detalhes do Bug')).toBeVisible();
    await expect(bugsReportPage.bodyContent).toContainText('Status: Aberto');
    await expect(bugsReportPage.btnResolve).toBeVisible();
    
    // resolving bug 1
    page.once('dialog', dialog => dialog.accept().catch(() => {}));
    await bugsReportPage.resolveBug();
    
    await expect(bugsReportPage.msgResolved).toBeVisible();
    await expect(bugsReportPage.bodyContent).toContainText('Status: Resolvido');

    await bugsReportPage.linkBackToBugList.click();

    await expect(bugsReportPage.getHeadingByText('Relatório de Bugs')).toBeVisible();
    await expect(bugsReportPage.bodyContent).toContainText('Projeto Alpha BugBugTeste3 Média ✔️');
    await expect(bugsReportPage.bodyContent).toContainText('Projeto Alpha BugBugTeste4 Baixa ❌');
    
    // resolving bug 2 and reopening it
    await bugsReportPage.viewBugDetailsFromReport('Projeto Alpha BugBugTeste4 Baixa ❌');
    
    page.once('dialog', dialog => dialog.accept().catch(() => {}));
    await bugsReportPage.resolveBug();
    
    await expect(bugsReportPage.msgResolved).toBeVisible();
    await expect(bugsReportPage.bodyContent).toContainText('Status: Resolvido');
    
    await bugsReportPage.linkBackToBugList.click();
    await expect(bugsReportPage.bodyContent).toContainText('Projeto Alpha BugBugTeste4 Baixa ✔️');
    
    await bugsReportPage.viewBugDetailsFromReport('Projeto Alpha BugBugTeste4 Baixa ✔️');
    
    page.once('dialog', dialog => dialog.accept().catch(() => {}));
    await bugsReportPage.reopenBug();
    
    await expect(bugsReportPage.msgReopened).toBeVisible();
    await expect(bugsReportPage.bodyContent).toContainText('Status: Aberto');
    
    await bugsReportPage.linkBackToBugList.click();
    
    // final status
    await expect(bugsReportPage.getHeadingByText('Relatório de Bugs')).toBeVisible();
    await expect(bugsReportPage.bodyContent).toContainText('Projeto Alpha BugBugTeste3 Média ✔️');
    await expect(bugsReportPage.bodyContent).toContainText('Projeto Alpha BugBugTeste4 Baixa ❌');
  });

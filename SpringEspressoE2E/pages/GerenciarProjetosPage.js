class GerenciarProjetosPage {
  constructor(page) {
    this.page = page;

    //Buttons
    this.btnSave = page.getByRole('button', { name: 'Salvar' });
    this.btnUpdate = page.getByRole('button', { name: 'Atualizar' });
    this.btnDelete = page.getByRole('button', { name: 'Excluir' });
    this.linkCancel = page.getByRole('link', { name: 'Cancelar' });
    this.linkBackToList = page.getByRole('link', { name: 'Voltar para a Lista' });
    this.linkBackToSessions = page.getByRole('link', { name: 'Voltar para Sessões' });
    this.linkMainMenu = page.getByRole('link', { name: 'Main Menu' });
  }

  //Methods
  async goToProjects() {
    await this.page.getByRole('link', { name: 'Ver Projetos' }).click();
  }

  //Project
  async startNewProject() {
    await this.page.getByRole('link', { name: 'Novo Projeto' }).click();
  }

  async fillProjectDetails(name, description, membersCheckboxes = []) {
    await this.page.getByRole('textbox', { name: 'Nome:' }).fill(name);
    await this.page.getByRole('textbox', { name: 'Descrição:' }).fill(description);
    for (const member of membersCheckboxes) {
      await this.page.getByRole('checkbox', { name: member }).check();
    }
  }

  async toggleMember(member, check = true) {
    const checkbox = this.page.getByRole('checkbox', { name: member });
    if (check) await checkbox.check();
    else await checkbox.uncheck();
  }

  async clickProjectAction(rowText, actionName) { //actionName = 'Ver Detalhes', 'Editar', 'Remover'
    await this.page.getByRole('row').filter({ hasText: rowText })
                   .getByRole('link', { name: actionName }).click();
  }

  async enterProjectFromUserTable(rowText) {
    await this.page.getByRole('row', { name: rowText }).getByRole('link').click();
  }

  //Session
  async accessStrategy(strategyName) {
    await this.page.getByRole('link', { name: strategyName }).click();
  }

  async startNewSession() {
    await this.page.getByRole('link', { name: '+ Nova Sessão' }).click();
  }

  async fillSessionDetails(description, durationMinutes, testerValue = null) {
    await this.page.getByRole('textbox', { name: 'Descrição:' }).fill(description);
    await this.page.getByRole('spinbutton', { name: 'Duração (em minutos):' }).fill(durationMinutes);
    if (testerValue) {
      await this.page.getByLabel('Tester Responsável:').selectOption(testerValue);
    }
  }

  async saveSession() {
    await this.page.getByRole('button', { name: 'Criar Sessão' }).click();
  }

  async accessSessionAction(rowText, actionName = null) {
    const row = this.page.getByRole('row', { name: rowText });
    if (actionName) {
        await row.getByRole('link', { name: actionName }).click();
    } else {
        await row.getByRole('link').first().click(); // Default User behavior
    }
  }

  async changeExecutionStatus(start = true) {
    const btnName = start ? 'Iniciar Execução' : 'Finalizar Sessão';
    await this.page.getByRole('button', { name: btnName }).click();
  }
  //Bug
  async startNewBug() {
    await this.page.getByRole('link', { name: 'Adicionar Bug' }).click();
  }

  async fillBugDetails(description, criticality, filePath = null) {
    await this.page.getByRole('textbox', { name: 'Descrição do Bug: *' }).fill(description);
    await this.page.getByLabel('Criticidade:').selectOption(criticality);
    if (filePath) {
      await this.page.getByRole('button', { name: 'Anexar Arquivo' }).setInputFiles(filePath);
    }
  }

  async saveBug() {
    await this.page.getByRole('button', { name: 'Cadastrar Bug' }).click();
  }
}

module.exports = { GerenciarProjetosPage };
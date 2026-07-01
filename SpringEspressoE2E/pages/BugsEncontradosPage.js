class BugsEncontradosPage {
  constructor(page) {
    this.page = page;

    //Buttons
    this.btnResolve = page.getByRole('button', { name: 'Marcar como Resolvido' });
    this.btnReopen = page.getByRole('button', { name: 'Reabrir Bug' });
    this.linkBackToBugList = page.getByRole('link', { name: 'Voltar para Lista de Bugs' });
    this.linkBack = page.getByRole('link', { name: 'Voltar' });
    this.btnViewImage = page.getByText('📷 Imagem');
    this.linkViewBugs = page.getByRole('link', { name: 'Ver Bugs' });

    //Msgs & locators
    this.bodyContent = page.locator('body'); 
    this.msgResolved = page.getByText('Bug marcado como resolvido!');
    this.msgReopened = page.getByText('Bug reaberto!');
    this.imgScreenshot = page.getByRole('img', { name: 'Screenshot do bug' });
  }

  //Methods
  getHeadingByText(titleText) {
    return this.page.getByRole('heading', { name: titleText });
  }

  async goToBugsReport() {
    await this.linkViewBugs.click();
  }

  async goBackToSession(sessionLinkName) {
    await this.page.getByRole('link', { name: sessionLinkName }).click();
  }

  async viewBugDetailsFromSession(bugName) {
    await this.page.getByRole('cell', { name: bugName }).click();
  }

  async viewBugDetailsFromReport(rowText) {
    await this.page.getByRole('row', { name: rowText }).getByRole('link').click();
  }

  async viewAttachedImage() {
    await this.btnViewImage.click();
  }

  async resolveBug() {
    await this.btnResolve.click();
  }

  async reopenBug() {
    await this.btnReopen.click();
  }
}

module.exports = { BugsEncontradosPage };
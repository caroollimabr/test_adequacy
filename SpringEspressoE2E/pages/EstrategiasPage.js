const { expect } = require('@playwright/test');

class EstrategiasPage {
  constructor(page) {
    this.page = page;

    //Buttons
    this.viewStrategiesButton = page.getByRole('link', { name: 'Ver Estratégias' });
    this.viewStrategiesButtonGuest = page.locator('#estrategias-convidado-button');
    this.mainMenuButton = page.getByRole('link', { name: 'Main Menu' });
    this.backToListButton = page.getByRole('link', { name: 'Voltar para a Lista' });
    this.backToHomeButton = page.getByRole('link', { name: 'Voltar para Home' });
    this.addStrategyButton = page.getByRole('link', { name: 'Adicionar Nova Estratégia' });
    this.saveStrategyButton = page.getByRole('button', { name: 'Salvar Estratégia' });
    this.cancelButton = page.getByRole('link', { name: 'Cancelar' });
    this.removeStrategyLink = page.getByRole('link', { name: 'Remover Esta Estratégia' });
    this.editStrategyLink = page.getByRole('link', { name: 'Editar esta estratégia' });
    this.exampleImageButton = page.getByRole('button', { name: 'Imagem do Exemplo (opcional)' });
    this.addExampleButton = page.getByRole('button', { name: 'Adicionar Novo Exemplo' });
    this.addTipButton = page.getByRole('button', { name: 'Adicionar Nova Dica' });
    this.removeDynamicFieldButton = page.getByRole('button', { name: 'Remover' });

    //Headings
    this.listHeading = page.getByRole('heading', { name: 'Lista de Estratégias' });
    this.detailsHeading = page.getByRole('heading', { name: 'Detalhes da Estratégia' });
    this.errorHeading = page.getByRole('heading', { name: 'Ocorreu um Erro!' });
    this.loginHeading = page.getByRole('heading', { name: 'Login' });

    //Inputs
    this.strategyNameInput = page.getByRole('textbox', { name: 'Nome da Estratégia' });
    this.descriptionInput = page.getByRole('textbox', { name: 'Descrição' });
    this.exampleInput = page.getByRole('textbox', { name: 'Descreva um exemplo prático' });
    this.tipInput = page.getByRole('textbox', { name: 'Escreva uma dica útil' });

    //Messages
    this.saveSuccessMessage = page.getByText('Estratégia salva com sucesso!');
    this.removeSuccessMessage = page.getByText('Estratégia removida com sucesso.');
  }

  //Methods
  async seeEstrategiasListGuest() {
    await this.viewStrategiesButtonGuest.click();
  }

  async seeEstrategiasList() {
    await this.viewStrategiesButton.click();
  }

  async openStrategy(name) {
    await this.page.getByRole('link', { name: name }).click();
  }

  async goBackToList() {
    await this.backToListButton.click();
  }

  async goToMainMenu() {
    await this.mainMenuButton.click();
  }

  async goBackToHomeOnError() {
    await this.backToHomeButton.click();
  }

  async startAddingStrategy() {
    await this.addStrategyButton.click();
  }

  async fillBasicInfo(name, description) {
    if (name) await this.strategyNameInput.fill(name);
    if (description) await this.descriptionInput.fill(description);
  }

  async fillExample(text, index = 0, imagePath = null) {
    await this.exampleInput.nth(index).fill(text);
    if (imagePath) {
      await this.page.locator('input[type="file"]').nth(index).setInputFiles(imagePath);
    }
  }

  async clickAddExample() {
    await this.addExampleButton.click();
  }

  async fillTip(text, index = 0) {
    await this.tipInput.nth(index).fill(text);
  }

  async clickAddTip() {
    await this.addTipButton.click();
  }

  async removeDynamicField(index) {
    await this.removeDynamicFieldButton.nth(index).click();
  }

  async saveStrategy() {
    await this.saveStrategyButton.click();
  }

  async cancelStrategy() {
    await this.cancelButton.click();
  }

  async removeCurrentStrategy() {
    this.page.once('dialog', dialog => {
      console.log(`Dialog message: ${dialog.message()}`);
      dialog.accept().catch(() => {});
    });
    await this.removeStrategyLink.click();
  }

  async editCurrentStrategy() {
    await this.editStrategyLink.click();
  }

  async openExampleImage() {
    const page1Promise = this.page.waitForEvent('popup');
    await this.page.getByRole('link', { name: 'Imagem do exemplo:' }).click();
    return await page1Promise;
  }

  //Assertions

  async expectListVisible() {
    await expect(this.listHeading).toBeVisible();
  }

  async expectStrategiesInBody(textOrList) {
    await expect(this.page.locator('body')).toContainText(textOrList);
  }

  async expectStrategyNotInBody(strategyName) {
    await expect(this.page.locator('body')).not.toContainText(strategyName);
  }

  async expectDetailsVisible() {
    await expect(this.detailsHeading).toBeVisible();
  }

  async expectLoginScreenVisible() {
    await expect(this.loginHeading).toBeVisible();
  }

  async expectAddStrategyButtonVisible() {
    await expect(this.addStrategyButton).toBeVisible();
  }

  async expectSaveSuccess() {
    await expect(this.saveSuccessMessage).toBeVisible();
  }

  async expectRemoveSuccess() {
    await expect(this.removeSuccessMessage).toBeVisible();
  }

  async expectStrategyLinkVisible(name) {
    await expect(this.page.getByRole('link', { name: name })).toBeVisible();
  }

  async expectInexistentStrategy(name) {
    await expect(this.page.getByRole('link', { name: name })).not.toBeVisible();
  }

  async expectErrorScreenVisible() {
    await expect(this.errorHeading).toBeVisible();
  }
}

module.exports = { EstrategiasPage };
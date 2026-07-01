const { test, expect } = require('../utils/join-fixtures');
const { EstrategiasPage } = require('../pages/EstrategiasPage');
const { LoginPage } = require('../pages/LoginPage');

let loginPage;
let estrategiasPage;

test.describe.configure({ mode: 'default' });

  test.beforeEach(async ({ page }) => {
    loginPage = new LoginPage(page);
    estrategiasPage = new EstrategiasPage(page);
    await loginPage.goto();
  });

  test('must see Estrategias list as a guest, without logging in', async ({ page }) => {
    //seeing Estrategias list
    await estrategiasPage.seeEstrategiasListGuest();

    await estrategiasPage.expectListVisible();
    await estrategiasPage.expectStrategiesInBody('Newbie Golden Path Noob Journey Completionist Stress Test Speedrun User Interface Neighboring Overtime Automated Testing');

    // seeing Estrategia details & going back to List
    await estrategiasPage.openStrategy('Golden Path');
    await estrategiasPage.expectDetailsVisible();
    await estrategiasPage.goBackToList();

    //seeing Estrategia details & going Home
    await estrategiasPage.openStrategy('Speedrun');
    await estrategiasPage.goToMainMenu();
    await estrategiasPage.expectLoginScreenVisible();
  });

  test('must see Estrategias list and details when logged as user, then go back Home', async ({ page }) => {
    //seeing Estrategias list
    await loginPage.login('user@user.com', 'user');
    await estrategiasPage.seeEstrategiasList();

    await estrategiasPage.expectListVisible();
    await estrategiasPage.expectStrategiesInBody('Newbie Golden Path Noob Journey Completionist Stress Test Speedrun User Interface Neighboring Overtime Automated Testing');
    
    // seeing Estrategia details & going Home
    await estrategiasPage.openStrategy('Stress Test');
    await estrategiasPage.expectDetailsVisible();
    
    await estrategiasPage.goToMainMenu();
    await loginPage.expectWelcomeMessage('user@user.com');
  });

  test('must see Estrategias list when logged as admin and add a new strategy with 2 examples and 2 tips', async ({ page }) => {
    //Seeing Estrategias list
    await loginPage.login('admin@admin.com', 'admin');
    await estrategiasPage.seeEstrategiasList();
    
    await estrategiasPage.expectListVisible();
    await estrategiasPage.expectAddStrategyButtonVisible();
    await estrategiasPage.expectStrategiesInBody('Newbie Golden Path Noob Journey Completionist Stress Test Speedrun User Interface Neighboring Overtime Automated Testing');

    //Adding a new estrategia
    await estrategiasPage.startAddingStrategy();
    await estrategiasPage.fillBasicInfo('EstrategiaTeste1', 'DescricaoTeste');
    
    // Example 1 
    await estrategiasPage.fillExample('ExemploTeste1', 0, 'bug.png');
    // Example 2
    await estrategiasPage.clickAddExample();
    await estrategiasPage.fillExample('ExemploTeste11', 1);
    
    // Tip 1
    await estrategiasPage.fillTip('DicaTeste1', 0);
    // Tip 2
    await estrategiasPage.clickAddTip();
    await estrategiasPage.fillTip('DicaTeste11', 1);
    
    await estrategiasPage.saveStrategy();

    await estrategiasPage.expectSaveSuccess();
    await estrategiasPage.expectStrategyLinkVisible('EstrategiaTeste1');
  });

  test('must see Estrategias list when logged as admin and cancel adding a new strategy', async ({ page }) => {
    //Seeing Estrategias list
    await loginPage.login('admin@admin.com', 'admin');
    await estrategiasPage.seeEstrategiasList();
    //Adding a new estrategia
    await estrategiasPage.expectListVisible();
    await estrategiasPage.expectAddStrategyButtonVisible();

    await estrategiasPage.startAddingStrategy();
    await estrategiasPage.fillBasicInfo('EstrategiaTeste2', 'DescricaoTeste2');
    await estrategiasPage.fillExample('ExemploTeste2', 0, 'bug.png');
    await estrategiasPage.fillTip('DicaTeste2', 0);
    
    //Canceling
    await estrategiasPage.cancelStrategy();
    
    await estrategiasPage.expectListVisible();
    await estrategiasPage.expectStrategyNotInBody('EstrategiaTeste2');
    
    await estrategiasPage.goToMainMenu();
    await loginPage.expectWelcomeMessage('admin@admin.com');
  });

  test('must remove an estrategia that has only the mandatory field filled when logged as an admin', async ({ page }) => {
    //Seeing Estrategias list
    await loginPage.login('admin@admin.com', 'admin');
    await estrategiasPage.seeEstrategiasList();
    //Adding a new estrategia
    await estrategiasPage.startAddingStrategy();
    await estrategiasPage.fillBasicInfo('EstrategiaTeste3', '');
    await estrategiasPage.saveStrategy();

    await estrategiasPage.expectSaveSuccess();
    await estrategiasPage.expectStrategyLinkVisible('EstrategiaTeste3');

    // Removing Strategy
    await estrategiasPage.openStrategy('EstrategiaTeste3');
    await estrategiasPage.expectDetailsVisible();
    
    await estrategiasPage.removeCurrentStrategy();
    
    await estrategiasPage.expectRemoveSuccess();
    await estrategiasPage.expectInexistentStrategy('EstrategiaTeste3');
    await estrategiasPage.expectStrategiesInBody('Lista de Estratégias');
  });

  test('must not edit an estrategia when logged as an admin because error screen is shown', async ({ page }) => {
    //Seeing Estrategias list
    await loginPage.login('admin@admin.com', 'admin');
    await estrategiasPage.seeEstrategiasList();
    //Adding a new estrategia
    await estrategiasPage.startAddingStrategy();
    await estrategiasPage.fillBasicInfo('EstrategiaTeste4', 'DescricaoTeste4');
    
    //Example 1 and 2
    await estrategiasPage.fillExample('ExemploTeste4', 0, 'bug.png');
    await estrategiasPage.clickAddExample();
    await estrategiasPage.fillExample('ExemploTeste44', 1);
    //Tip 1 and 2
    await estrategiasPage.fillTip('DicaTeste4', 0);
    await estrategiasPage.clickAddTip();
    await estrategiasPage.fillTip('DicaTeste44', 1);
    
    //Removing Example 2 and Tip 2 (checking if buttons work)
    await estrategiasPage.removeDynamicField(3);
    await estrategiasPage.removeDynamicField(1);

    await estrategiasPage.saveStrategy();
    
    await estrategiasPage.expectSaveSuccess();
    await estrategiasPage.openStrategy('EstrategiaTeste4');
    
    await estrategiasPage.expectStrategiesInBody('EstrategiaTeste4');
    await estrategiasPage.expectStrategiesInBody('DescricaoTeste4');
    await estrategiasPage.expectStrategiesInBody('ExemploTeste4');
    await estrategiasPage.expectStrategiesInBody('DicaTeste4');
    await estrategiasPage.expectStrategyNotInBody('ExemploTeste44');
    await estrategiasPage.expectStrategyNotInBody('DicaTeste44');

    //clicking on image opens it in a new tab
    const popupPage = await estrategiasPage.openExampleImage();
    await expect(popupPage.getByRole('img')).toBeVisible();

    //trying to edit it > error page
    await estrategiasPage.editCurrentStrategy();
    await estrategiasPage.expectErrorScreenVisible();
    //going back Home through Error page
    await estrategiasPage.goBackToHomeOnError();
    await loginPage.expectWelcomeMessage('admin@admin.com');
  });

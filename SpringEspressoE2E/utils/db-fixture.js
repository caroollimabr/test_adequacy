const { test: base } = require('@playwright/test');
const mysql = require('mysql2/promise');

const dbFixture = base.extend({
  
  dbClient: [
    async ({}, use) => {
      console.log('Conectando ao MySQL');
      const connection = await mysql.createConnection({
        user: 'root',
        host: 'localhost',
        database: 'espresso_testing_jpa_db',
        password: '123456',
        port: 3306,
      });

      await use(connection);
      await connection.end();
      console.log('Conexão ao MySQL encerrada');
    },
    { scope: 'worker', auto: true }
  ],

  dbCleaner: [
    async ({ dbClient }, use) => {
      const cleanDB = async () => {
        console.log('Limpando DB');
        try {
          await dbClient.execute("DELETE FROM bugs WHERE descricao like '%Teste%';");
          await dbClient.execute("DELETE FROM sessoes WHERE descricao like '%Teste%'");
          await dbClient.execute("DELETE FROM exemplos WHERE texto like '%Teste%';");
          await dbClient.execute("DELETE FROM dicas WHERE texto like '%DicaTeste%';");
          await dbClient.execute("DELETE FROM projeto_estrategias WHERE projeto_id > 2;");
          await dbClient.execute("DELETE FROM estrategias WHERE nome like '%EstrategiaTeste%';");
          await dbClient.execute("DELETE FROM bugs WHERE descricao like '%Bug%';");
          await dbClient.execute("DELETE FROM sessoes WHERE descricao like '%Teste%'");
          await dbClient.execute("DELETE FROM projeto_membros WHERE projeto_id > 2");
          await dbClient.execute("DELETE FROM projeto_estrategias WHERE projeto_id > 2");
          await dbClient.execute("DELETE FROM projeto WHERE descricao like '%TesteAdmin%'");
          await dbClient.execute("DELETE FROM bugs WHERE descricao like '%Bug%';");
          await dbClient.execute("DELETE FROM sessoes WHERE descricao like '%Teste%'");
        } catch (error) {
          console.error('Erro ao limpar banco de dados:', error);
        }
      };

      await use(cleanDB);
    },
    { scope: 'worker' }
  ]
});

module.exports = { dbFixture };
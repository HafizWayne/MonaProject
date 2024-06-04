const mysql = require('mysql');

const connection = mysql.createConnection({
  host: 'YOUR_DB_HOST',
  user: 'YOUR_DB_USER',
  password: 'YOUR_DB_PASSWORD',
  database: 'YOUR_DB_NAME'
});

connection.connect((err) => {
  if (err) {
    console.error('Error connecting to database:', err.stack);
    return;
  }
  console.log('Connected to database as id', connection.threadId);
});

module.exports = connection;

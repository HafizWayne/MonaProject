const mysql = require('mysql');

const connection = mysql.createConnection({
  host: '34.143.244.48',
  user: 'root',
  password: '1234',
  database: 'mona'
});

connection.connect((err) => {
  if (err) {
    console.error('Error connecting to database:', err.stack);
    return;
  }
  console.log('Connected to database as id', connection.threadId);
});

module.exports = connection;

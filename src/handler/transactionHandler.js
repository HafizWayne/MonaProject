const db = require('../db/database');

const createTransactions = (req, res) => {
  const { date, credentials, category, amount, title, action } = req.body;

  if (!date || !credentials || !category || !amount || !title || !action) {
    return res.status(400).send('Missing fields');
  }

  const newTransaction = { date, credentials, category, amount, title, action };

  db.query('INSERT INTO transactions SET ?', newTransaction, (err, results) => {
    if (err) {
      console.error('Error inserting transaction:', err);
      return res.status(500).send('Server error');
    }
    newTransaction.id = results.insertId; // Set the ID of the new transaction from the database

    // Determine the SQL query for updating user's balance
    let updateQuery;
    let updateValues;

    if (action === 'income') {
      updateQuery = 'UPDATE users SET total_balance = total_balance + ? WHERE id = ?';
      updateValues = [amount, credentials];
    } else if (action === 'expense') {
      updateQuery = 'UPDATE users SET total_balance = total_balance - ?, total_expense = total_expense + ? WHERE id = ?';
      updateValues = [amount, amount, credentials];
    }

    if (updateQuery) {
      db.query(updateQuery, updateValues, (updateErr) => {
        if (updateErr) {
          console.error('Error updating user balance:', updateErr);
          return res.status(500).send('Server error');
        }
        res.status(201).send(newTransaction);
      });
    } else {
      res.status(201).send(newTransaction);
    }
  });
};

const getAllTransactions = (req, res) => {
  const query = 'SELECT id, DATE_FORMAT(date, "%Y-%m-%d") as date, credentials, category, amount, title, action FROM transactions';

  db.query(query, (err, results) => {
    if (err) {
      return res.status(500).send('Server error');
    }
    res.status(200).json(results);
  });
};

module.exports = {
  createTransactions,
  getAllTransactions
};

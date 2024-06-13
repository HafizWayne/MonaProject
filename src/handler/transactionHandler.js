const { transactions } = require('../models/transaction');
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
      res.status(201).send(newTransaction);
    });
  };

  const getAllTransactions = (req, res) => {
    const query = 'SELECT * FROM transactions';
  
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
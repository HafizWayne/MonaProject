const { transactions } = require('../models/transaction');

const createTransactions = (req, res) => {
    const { date, credentials, category, amount, title, action } = req.body;
  
    if (!date || !credentials || !category || !amount || !title || !action) {
      return res.status(400).send('Missing fields');
    }
  
    const nextId = transactions.length > 0 ? transactions[transactions.length - 1].id + 1 : 1;
  
    const newTransaction = { id: nextId, date, credentials, category, amount, title, action };
    transactions.push(newTransaction);
    // db.query('INSERT INTO transactions SET ?', newTransaction, (err, results) => {
    //   if (err) throw err;
    //   console.log('Data saved:', results);
    // });
    res.status(201).send(newTransaction);
  };

  const getAllTransactions = (req, res) => {
    res.status(200).json(transactions);
  };

  module.exports = {
    createTransactions,
    getAllTransactions
  };
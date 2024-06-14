const db = require('../db/database');

const createTransactions = (req, res) => {
  const { date, credentials, category, amount, title, action } = req.body;

  console.log(req.body); // Log request body for debugging

  if (!date || !credentials || !category || !amount || !title || !action) {
    return res.status(400).send('Missing fields');
  }

  // Normalize action to lower case
  const normalizedAction = action.toLowerCase();

  const validActions = ['income', 'expense'];
  if (!validActions.includes(normalizedAction)) {
    return res.status(400).send('Invalid action');
  }

  const newTransaction = { date, credentials, category, amount, title, action: normalizedAction };

  db.query('INSERT INTO transactions SET ?', newTransaction, (err, results) => {
    if (err) {
      console.error('Error inserting transaction:', err);
      return res.status(500).send('Server error');
    }

    // Determine the update query based on the action
    let updateQuery;
    let updateParams;

    if (normalizedAction === 'income') {
      updateQuery = 'UPDATE users SET total_balance = total_balance + ? WHERE id = ?';
      updateParams = [amount, credentials];
    } else if (normalizedAction === 'expense') {
      updateQuery = 'UPDATE users SET total_balance = total_balance - ?, total_expense = total_expense + ? WHERE id = ?';
      updateParams = [amount, amount, credentials];
    }

    // Execute the update query
    db.query(updateQuery, updateParams, (updateErr, updateResults) => {
      if (updateErr) {
        console.error('Error updating user balance:', updateErr);
        return res.status(500).send('Server error while updating user balance');
      }

      if (updateResults.affectedRows === 0) {
        return res.status(404).send('User not found');
      }

      res.status(201).send(newTransaction);
    });
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

const db = require('../db/database');

// Create transaction
const createTransactions = (req, res) => {
  const { date, credentials, category, amount, title, action, food_category } = req.body;

  if (!date || !credentials || !category || !amount || !title || !action || !food_category) {
    return res.status(400).send('Missing fields');
  }

  // Normalize action to lowercase for comparison
  const normalizedAction = action.toLowerCase();
  const validActions = ['income', 'expense'];

  if (!validActions.includes(normalizedAction)) {
    return res.status(400).send('Invalid action');
  }

  const newTransaction = { date, credentials, category, amount, title, action: normalizedAction, food_category };

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

      newTransaction.id = results.insertId;
      res.status(201).send(newTransaction);
    });
  });
};

// Get all transactions
const getAllTransactions = (req, res) => {
  const query = 'SELECT id, DATE_FORMAT(date, "%Y-%m-%d") as date, credentials, category, amount, title, action, food_category FROM transactions';

  db.query(query, (err, results) => {
    if (err) {
      console.error('Error fetching transactions:', err);
      return res.status(500).send('Server error');
    }
    res.status(200).json(results);
  });
};

// Get transaction by credentials
const getTransactionById = (req, res) => {
  const credentials = req.params.credentials;

  const query = 'SELECT id, DATE_FORMAT(date, "%Y-%m-%d") as date, credentials, category, amount, title, action, food_category FROM transactions WHERE credentials = ?';
  
  db.query(query, [credentials], (err, results) => {
    if (err) {
      console.error('Error fetching transactions:', err);
      return res.status(500).send('Server error');
    }
    if (results.length === 0) {
      return res.status(404).send('Transactions not found for the given credentials');
    }
    res.status(200).json(results);
  });
};

// Update transaction
const updateTransaction = (req, res) => {
  const id = parseInt(req.params.id, 10);
  const { date, category, amount, title, action, food_category } = req.body;

  if (!date || !category || !amount || !title || !action || !food_category) {
    return res.status(400).send('Missing fields');
  }

  const query = 'UPDATE transactions SET date = ?, category = ?, amount = ?, title = ?, action = ?, food_category = ? WHERE id = ?';

  db.query(query, [date, category, amount, title, action, food_category, id], (err, results) => {
    if (err) {
      console.error('Error updating transaction:', err);
      return res.status(500).send('Server error');
    }
    if (results.affectedRows === 0) {
      return res.status(404).send('Transaction not found');
    }
    res.status(200).send({ id, date, category, amount, title, action, food_category });
  });
};

// Delete transaction
const deleteTransaction = (req, res) => {
  const id = parseInt(req.params.id, 10);

  const query = 'DELETE FROM transactions WHERE id = ?';

  db.query(query, [id], (err, results) => {
    if (err) {
      console.error('Error deleting transaction:', err);
      return res.status(500).send('Server error');
    }
    if (results.affectedRows === 0) {
      return res.status(404).send('Transaction not found');
    }
    res.status(204).send();
  });
};

module.exports = {
  createTransactions,
  getAllTransactions,
  getTransactionById,
  updateTransaction,
  deleteTransaction
};

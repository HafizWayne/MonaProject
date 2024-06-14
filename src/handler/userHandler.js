const db = require('../db/database');

const createUsers = (req, res) => {
    const { id, nama, total_balance, total_expense, total_emergency } = req.body;
  
    if (!id || !nama || total_balance === undefined || total_expense === undefined || total_emergency === undefined) {
      return res.status(400).send('Missing fields');
    }
  
    const newUser = { id, nama, total_balance, total_expense, total_emergency };
  
    db.query('INSERT INTO users SET ?', newUser, (err, results) => {
      if (err) {
        console.error('Error inserting user:', err);
        return res.status(500).send('Server error');
      }
      res.status(201).send(newUser);
    });
  };

const getAllUsers = (req, res) => {
  const query = 'SELECT * FROM users';

  db.query(query, (err, results) => {
    if (err) {
      console.error('Error fetching users:', err);
      return res.status(500).send('Server error');
    }
    res.status(200).json(results);
  });
};

const getUsersById = (req, res) => {
  const id = req.params.id;

  const query = 'SELECT * FROM users WHERE id = ?';
  
  db.query(query, [id], (err, results) => {
    if (err) {
      console.error('Error fetching user:', err);
      return res.status(500).send('Server error');
    }
    if (results.length === 0) {
      return res.status(404).send('User not found for the given id');
    }
    res.status(200).json(results[0]);
  });
};

module.exports = {
  createUsers,
  getAllUsers,
  getUsersById
};

const db = require('../db/database');

const createUsers = (req, res) => {
    const { credentials, nama, total_balance, total_expense, total_emergency, dana_maksimal, total_makan } = req.body;
  
    if (!credentials || !nama || total_balance === undefined || total_expense === undefined || total_emergency === undefined || dana_maksimal === undefined || total_makan === undefined) {
      return res.status(400).send('Missing fields');
    }
  
    const newUser = { credentials, nama, total_balance, total_expense, total_emergency, dana_maksimal, total_makan };
  
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

const getUsersBycredentials = (req, res) => {
  const credentials = req.params.credentials;

  const query = 'SELECT * FROM users WHERE credentials = ?';
  
  db.query(query, [credentials], (err, results) => {
    if (err) {
      console.error('Error fetching user:', err);
      return res.status(500).send('Server error');
    }
    if (results.length === 0) {
      return res.status(404).send('User not found for the given credentials');
    }
    res.status(200).json(results[0]);
  });
};

module.exports = {
  createUsers,
  getAllUsers,
  getUsersBycredentials
};

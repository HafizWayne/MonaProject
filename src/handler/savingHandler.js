const db = require('../db/database'); // Adjusted to your provided path

const createSaving = (req, res) => {
  const { date, credentials, amount, title } = req.body;

  if (!date || !credentials || !amount || !title) {
    return res.status(400).send('Missing fields');
  }

  const newSaving = { date, credentials, amount, title };
  
  db.query('INSERT INTO savings SET ?', newSaving, (err, results) => {
    if (err) {
      console.error('Error inserting saving:', err);
      return res.status(500).send('Server error');
    }
    newSaving.id = results.insertId; // Set the ID of the new saving from the database

    // Update the total_emergency in the users table
    const updateQuery = 'UPDATE users SET total_emergency = total_emergency + ? WHERE id = ?';
    db.query(updateQuery, [amount, credentials], (updateErr) => {
      if (updateErr) {
        console.error('Error updating user total_emergency:', updateErr);
        return res.status(500).send('Server error');
      }
      res.status(201).send(newSaving);
    });
  });
};

const getAllSavings = (req, res) => {
  const query = 'SELECT id, DATE_FORMAT(date, "%Y-%m-%d") as date, credentials, amount, title FROM savings';

  db.query(query, (err, results) => {
    if (err) {
      console.error('Error fetching savings:', err);
      return res.status(500).send('Server error');
    }
    res.status(200).json(results);
  });
};

const getSavingById = (req, res) => {
  const credentials = req.params.credentials;

  const query = 'SELECT id, DATE_FORMAT(date, "%Y-%m-%d") as date, credentials, amount, title FROM savings WHERE credentials = ?';
  
  db.query(query, [credentials], (err, results) => {
    if (err) {
      console.error('Error fetching savings:', err);
      return res.status(500).send('Server error');
    }
    if (results.length === 0) {
      return res.status(404).send('Savings not found for the given credentials');
    }
    res.status(200).json(results);
  });
};

const updateSaving = (req, res) => {
  const id = parseInt(req.params.id, 10);
  const { date, amount, title } = req.body;

  if (!date || !amount || !title) {
    return res.status(400).send('Missing fields');
  }

  const query = 'UPDATE savings SET date = ?, amount = ?, title = ? WHERE id = ?';

  db.query(query, [date, amount, title, id], (err, results) => {
    if (err) {
      console.error('Error updating saving:', err);
      return res.status(500).send('Server error');
    }
    if (results.affectedRows === 0) {
      return res.status(404).send('Saving not found');
    }
    res.status(200).send({ id, date, amount, title });
  });
};

const deleteSaving = (req, res) => {
  const id = parseInt(req.params.id, 10);

  const query = 'DELETE FROM savings WHERE id = ?';

  db.query(query, [id], (err, results) => {
    if (err) {
      console.error('Error deleting saving:', err);
      return res.status(500).send('Server error');
    }
    if (results.affectedRows === 0) {
      return res.status(404).send('Saving not found');
    }
    res.status(204).send();
  });
};

module.exports = {
  createSaving,
  getAllSavings,
  getSavingById,
  updateSaving,
  deleteSaving
};

const { savings } = require('../models/saving');

const createSaving = (req, res) => {
  const { date, credentials, amount, title } = req.body;

  if (!date || !credentials || !amount || !title) {
    return res.status(400).send('Missing fields');
  }

  const nextId = savings.length > 0 ? savings[savings.length - 1].id + 1 : 1;

  const newSaving = { id: nextId, date, credentials, amount, title };
  savings.push(newSaving);
  // db.query('INSERT INTO savings SET ?', newSaving, (err, results) => {
  //   if (err) throw err;
  //   console.log('Data saved:', results);
  // });
  res.status(201).send(newSaving);
};


const getAllSavings = (req, res) => {
  res.status(200).json(savings);
};

const getSavingById = (req, res) => {
  const credentials = req.params.credentials;
  const userSavings = savings.filter(s => s.credentials === credentials);

  if (userSavings.length === 0) {
    return res.status(404).send('Savings not found for the given credentials');
  }

  res.status(200).json(userSavings);
};

const updateSaving = (req, res) => {
  const id = parseInt(req.params.id, 10);
  const { date, amount, title } = req.body;
  const savingIndex = savings.findIndex(s => s.id === id);

  if (savingIndex === -1) {
    return res.status(404).send('Saving not found');
  }

  if (!date || !amount || !title) {
    return res.status(400).send('Missing fields');
  }

  const updatedSaving = { id, date, amount, title };
  savings[savingIndex] = updatedSaving;
  res.status(200).send(updatedSaving);
};

const deleteSaving = (req, res) => {
  const id = parseInt(req.params.id, 10);
  const savingIndex = savings.findIndex(s => s.id === id);

  if (savingIndex === -1) {
    return res.status(404).send('Saving not found');
  }

  savings.splice(savingIndex, 1);
  res.status(204).send();
};

module.exports = {
  createSaving,
  getAllSavings,
  getSavingById,
  updateSaving,
  deleteSaving
};
